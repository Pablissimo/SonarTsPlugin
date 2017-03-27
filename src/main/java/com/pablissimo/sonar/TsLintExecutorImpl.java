package com.pablissimo.sonar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;
import org.sonar.api.utils.command.StringStreamConsumer;

public class TsLintExecutorImpl implements TsLintExecutor {
    public static final int MAX_COMMAND_LENGTH = 4096;
    private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);

    private boolean mustQuoteSpaceContainingPaths = false;
    private TempFolder tempFolder;

    public TsLintExecutorImpl(System2 system, TempFolder tempFolder) {
        this.mustQuoteSpaceContainingPaths = system.isOsWindows();
        this.tempFolder = tempFolder;
    }

    private String preparePath(String path) {
        if (path == null) {
            return "";
        }
        else if (path.contains(" ") && this.mustQuoteSpaceContainingPaths) {
            return '"' + path + '"';
        }
        else {
            return path;
        }
    }

    private Command getBaseCommand(TsLintExecutorConfig config, String tempPath) {
        Command command =
                Command
                .create(config.getPathToNode())
                .addArgument(this.preparePath(config.getPathToTsLint()))
                .addArgument("--format")
                .addArgument("json");

        String rulesDir = config.getRulesDir();
        if (rulesDir != null && rulesDir.length() > 0) {
            command
                .addArgument("--rules-dir")
                .addArgument(this.preparePath(rulesDir));
        }

        if (tempPath != null && tempPath.length() > 0) {
            command
                .addArgument("--out")
                .addArgument(this.preparePath(tempPath));
        }

        command
            .addArgument("--config")
            .addArgument(this.preparePath(config.getConfigFile()));

        if (config.useTsConfigInsteadOfFileList()) {
            command
                .addArgument("--project")
                .addArgument(this.preparePath(config.getPathToTsConfig()));
        }

        if (config.shouldPerformTypeCheck()) {
            command
                .addArgument("--type-check");
        }

        command.setNewShell(false);

        return command;
    }

    @Override
    public List<String> execute(TsLintExecutorConfig config, List<String> files) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        else if (files == null) {
            throw new IllegalArgumentException("files");
        }

        if (config.useExistingTsLintOutput()) {
            LOG.debug("Running with existing JSON file '{}' instead of calling tslint", config.getPathToTsLintOutput());
            List<String> toReturn = new ArrayList<>();
            toReturn.add(this.getFileContent(new File(config.getPathToTsLintOutput())));
            return toReturn;
        }

        // New up a command that's everything we need except the files to process
        // We'll use this as our reference for chunking up files, if we need to
        File tslintOutputFile = this.tempFolder.newFile();
        String tslintOutputFilePath = tslintOutputFile.getAbsolutePath();
        Command baseCommand = getBaseCommand(config, tslintOutputFilePath);

        LOG.debug("Using a temporary path for TsLint output: {}", tslintOutputFilePath);

        StringStreamConsumer stdOutConsumer = new StringStreamConsumer();
        StringStreamConsumer stdErrConsumer = new StringStreamConsumer();

        List<String> toReturn = new ArrayList<>();

        if (config.useTsConfigInsteadOfFileList()) {
            LOG.debug("Running against a single project JSON file");

            // If we're being asked to use a tsconfig.json file, it'll contain
            // the file list to lint - so don't batch, and just run with it
            toReturn.add(this.getCommandOutput(baseCommand, stdOutConsumer, stdErrConsumer, tslintOutputFile, config.getTimeoutMs()));
        }
        else {
            int baseCommandLength = baseCommand.toCommandLine().length();
            int availableForBatching = MAX_COMMAND_LENGTH - baseCommandLength;

            List<List<String>> batches = new ArrayList<>();
            List<String> currentBatch = new ArrayList<>();
            batches.add(currentBatch);

            int currentBatchLength = 0;
            for (int i = 0; i < files.size(); i++) {
                String nextPath = this.preparePath(files.get(i).trim());

                // +1 for the space we'll be adding between filenames
                if (currentBatchLength + nextPath.length() + 1 > availableForBatching) {
                    // Too long to add to this batch, create new
                    currentBatch = new ArrayList<>();
                    currentBatchLength = 0;
                    batches.add(currentBatch);
                }

                currentBatch.add(nextPath);
                currentBatchLength += nextPath.length() + 1;
            }

            LOG.debug("Split {} files into {} batches for processing", files.size(), batches.size());

            for (int i = 0; i < batches.size(); i++) {
                List<String> thisBatch = batches.get(i);

                Command thisCommand = getBaseCommand(config, tslintOutputFilePath);

                for (int fileIndex = 0; fileIndex < thisBatch.size(); fileIndex++) {
                    thisCommand.addArgument(thisBatch.get(fileIndex));
                }

                LOG.debug("Executing TsLint with command: {}", thisCommand.toCommandLine());

                // Timeout is specified per file, not per batch (which can vary a lot)
                // so multiply it up
                toReturn.add(this.getCommandOutput(thisCommand, stdOutConsumer, stdErrConsumer, tslintOutputFile, config.getTimeoutMs() * thisBatch.size()));
            }
        }

        return toReturn;
    }

    private String getCommandOutput(Command thisCommand, StreamConsumer stdOutConsumer, StreamConsumer stdErrConsumer, File tslintOutputFile, Integer timeoutMs) {
        this.createExecutor().execute(thisCommand, stdOutConsumer, stdErrConsumer, timeoutMs);

        return getFileContent(tslintOutputFile);
    }

    private String getFileContent(File tslintOutputFile) {
        StringBuilder outputBuilder = new StringBuilder();

        try {
            BufferedReader reader = this.getBufferedReaderForFile(tslintOutputFile);

            String str;
            while ((str = reader.readLine()) != null) {
                outputBuilder.append(str);
            }

            reader.close();

            return outputBuilder.toString();
        }
        catch (IOException ex) {
            LOG.error("Failed to re-read TsLint output", ex);
        }

        return "";
    }

    protected BufferedReader getBufferedReaderForFile(File file) throws IOException {
        return new BufferedReader(
                new InputStreamReader(
                           new FileInputStream(file), "UTF8"));
    }

    protected CommandExecutor createExecutor() {
        return CommandExecutor.create();
    }
}
