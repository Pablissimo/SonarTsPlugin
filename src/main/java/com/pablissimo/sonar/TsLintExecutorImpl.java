package com.pablissimo.sonar;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

public class TsLintExecutorImpl implements TsLintExecutor {
    public static final int MAX_COMMAND_LENGTH = 4096;
    private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);

    private StringBuilder stdOut;
    private StringBuilder stdErr;

    private boolean mustQuoteSpaceContainingPaths = false;
    
    public TsLintExecutorImpl(System2 system) {
        this.mustQuoteSpaceContainingPaths = system.isOsWindows();
    }
    
    public String preparePath(String path) {
        if (path == null) {
            return null;
        }
        else if (path.contains(" ") && this.mustQuoteSpaceContainingPaths) {
            return '"' + path + '"';
        }
        else {
            return path;
        }
    }
    
    private Command getBaseCommand(String pathToTsLint, String configFile, String rulesDir) {
        Command command =
                Command
                .create("node")
                .addArgument(this.preparePath(pathToTsLint))
                .addArgument("--format")
                .addArgument("json");

        if (rulesDir != null && rulesDir.length() > 0) {
            command
                .addArgument("--rules-dir")
                .addArgument(this.preparePath(rulesDir));
        }

        command
            .addArgument("--config")
            .addArgument(this.preparePath(configFile))
            .setNewShell(false);

        return command;
    }

    public String execute(String pathToTsLint, String configFile, String rulesDir, List<String> files, Integer timeoutMs) {
        // New up a command that's everything we need except the files to process
        // We'll use this as our reference for chunking up files
        int baseCommandLength = getBaseCommand(pathToTsLint, configFile, rulesDir).toCommandLine().length();
        int availableForBatching = MAX_COMMAND_LENGTH - baseCommandLength;

        List<List<String>> batches = new ArrayList<List<String>>();
        List<String> currentBatch = new ArrayList<String>();
        batches.add(currentBatch);

        int currentBatchLength = 0;
        for (int i = 0; i < files.size(); i++) {
            String nextPath = this.preparePath(files.get(i).trim());

            // +1 for the space we'll be adding between filenames
            if (currentBatchLength + nextPath.length() + 1 > availableForBatching) {
                // Too long to add to this batch, create new
                currentBatch = new ArrayList<String>();
                currentBatchLength = 0;
                batches.add(currentBatch);
            }

            currentBatch.add(nextPath);
            currentBatchLength += nextPath.length() + 1;
        }

        LOG.debug("Split " + files.size() + " files into " + batches.size() + " batches for processing");

        this.stdOut = new StringBuilder();
        this.stdErr = new StringBuilder();

        StreamConsumer stdOutConsumer = new StreamConsumer() {
            public void consumeLine(String line) {
                stdOut.append(line);
            }
        };

        StreamConsumer stdErrConsumer = new StreamConsumer() {
            public void consumeLine(String line) {
                LOG.error("TsLint Err: " + line);
                stdErr.append(line + "\n");
            }
        };

        for (int i = 0; i < batches.size(); i++) {
            List<String> thisBatch = batches.get(i);
            Command thisCommand = getBaseCommand(pathToTsLint, configFile, rulesDir);

            for (int fileIndex = 0; fileIndex < thisBatch.size(); fileIndex++) {
                thisCommand.addArgument(thisBatch.get(fileIndex));
            }

            LOG.debug("Executing TsLint with command: " + thisCommand.toCommandLine());

            // Timeout is specified per file, not per batch (which can vary a lot)
            // so multiply it up
            this.createExecutor().execute(thisCommand, stdOutConsumer, stdErrConsumer, timeoutMs * thisBatch.size());
        }

        String rawOutput = stdOut.toString();
        
        // TsLint returns nonsense for its JSON output when faced with multiple files
        // so we need to fix it up before we do anything else
        return "[" + rawOutput.replaceAll("\\]\\[", "],[") + "]";
    }

    protected CommandExecutor createExecutor() {
        return CommandExecutor.create();
    }
}
