package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.EsLintExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;
import org.sonar.api.utils.command.StringStreamConsumer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class EsLintExecutorImpl implements EsLintExecutor {
    public static final int MAX_COMMAND_LENGTH = 4096;
    private static final Logger LOG = LoggerFactory.getLogger(EsLintExecutorImpl.class);
    private final TempFolder tempFolder;
    private final boolean mustQuoteSpaceContainingPaths;

    public EsLintExecutorImpl(System2 system, TempFolder tempFolder) {
        this.mustQuoteSpaceContainingPaths = system.isOsWindows();
        this.tempFolder = tempFolder;
    }

    protected BufferedReader getBufferedReaderForFile(File file) throws FileNotFoundException, UnsupportedEncodingException {
        return new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file), "UTF8"));
    }

    protected CommandExecutor createExecutor() {
        return CommandExecutor.create();
    }

    private String preparePath(String path) {
        if (path == null) {
            return null;
        } else if (path.contains(" ") && this.mustQuoteSpaceContainingPaths) {
            return '"' + path + '"';
        } else {
            return path;
        }
    }

    private Command getBaseCommand(EsLintExecutorConfig config, String tempPath) {
        Command command =
                Command
                        .create("node")
                        .addArgument(this.preparePath(config.getPathToEsLint()));
        command
                .addArgument("-f")
                .addArgument("json");

        String rulesDir = config.getRulesDir();
        if (rulesDir != null && !rulesDir.isEmpty()) {
            command
                    .addArgument("--rules-dir")
                    .addArgument(this.preparePath(rulesDir));
        }

        if (tempPath != null && !tempPath.isEmpty()) {
            command
                    .addArgument("--output-file")
                    .addArgument(this.preparePath(tempPath));
        }

        command
                .addArgument("--config")
                .addArgument(this.preparePath(config.getConfigFile()));


        command.setNewShell(false);

        return command;
    }

    @Override
    public List<String> execute(EsLintExecutorConfig config, List<String> files) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        if (files == null) {
            throw new IllegalArgumentException("files");
        }

        // New up a command that's everything we need except the files to process
        // We'll use this as our reference for chunking up files, if we need to
        File eslintOutputFile = this.tempFolder.newFile();
        String eslintOutputFilePath = eslintOutputFile.getAbsolutePath();
        Command baseCommand = this.getBaseCommand(config, eslintOutputFilePath);

        LOG.debug("Using a temporary path for EsLint output: {}", eslintOutputFilePath);

        StreamConsumer stdOutConsumer = new StringStreamConsumer();
        StreamConsumer stdErrConsumer = new StringStreamConsumer();

        List<String> toReturn = new ArrayList<>(100);
        int baseCommandLength = baseCommand.toCommandLine().length();
        int availableForBatching = MAX_COMMAND_LENGTH - baseCommandLength;

        List<List<String>> batches = new ArrayList<>(100);
        List<String> currentBatch = new ArrayList<>();
        batches.add(currentBatch);

        int currentBatchLength = 0;
        for (int i = 0, ni = files.size(); i < ni; i++) {
            String nextPath = this.preparePath(files.get(i).trim());

            // +1 for the space we'll be adding between filenames
            if (currentBatchLength + nextPath.length() + 1 > availableForBatching) {
                // Too long to add to this batch, create new
                currentBatch = new ArrayList<>(100);
                currentBatchLength = 0;
                batches.add(currentBatch);
            }

            currentBatch.add(nextPath);
            currentBatchLength += nextPath.length() + 1;
        }

        LOG.debug("Split {} files into  {} batches for processing", files.size(), batches.size());

        for (int i = 0, ni = batches.size(); i < ni; i++) {
            StringBuilder outputBuilder = new StringBuilder();
            List<String> thisBatch = batches.get(i);
            Command thisCommand = this.getBaseCommand(config, eslintOutputFilePath);

            for (int fileIndex = 0, nf = thisBatch.size(); fileIndex < nf; fileIndex++) {
                thisCommand.addArgument(thisBatch.get(fileIndex));
            }

            LOG.debug("Executing EsLint with command: {}", thisCommand.toCommandLine());

            // Timeout is specified per file, not per batch (which can vary a lot)
            // so multiply it up
            String commandOutput = this.getCommandOutput(thisCommand, stdOutConsumer, stdErrConsumer, eslintOutputFile, config.getTimeoutMs() * thisBatch.size());
            toReturn.add(commandOutput);
        }

        return toReturn;
    }

    private String getCommandOutput(Command thisCommand, StreamConsumer stdOutConsumer, StreamConsumer stdErrConsumer, File tslintOutputFile, Integer timeoutMs) {
        LOG.debug("Executing EsLint with command: {}", thisCommand.toCommandLine());

        // Timeout is specified per file, not per batch (which can vary a lot)
        // so multiply it up
        this.createExecutor().execute(thisCommand, stdOutConsumer, stdErrConsumer, timeoutMs);

        StringBuilder outputBuilder = new StringBuilder();

        try (final BufferedReader reader = this.getBufferedReaderForFile(tslintOutputFile)) {


            String str;
            //noinspection NestedAssignment
            while ((str = reader.readLine()) != null) {
                outputBuilder.append(str);
            }


            return outputBuilder.toString();
        } catch (IOException ex) {
            LOG.error("Failed to re-read EsLint output", ex);
        }

        return "";
    }
}
