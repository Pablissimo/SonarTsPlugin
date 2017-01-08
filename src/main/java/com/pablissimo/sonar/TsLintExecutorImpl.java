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
            return null;
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
                .create("node")
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
            .addArgument(this.preparePath(config.getConfigFile()))
            .setNewShell(false);

        return command;
    }

    public List<String> execute(TsLintExecutorConfig config, List<String> files) {
        if (config == null) {
            throw new IllegalArgumentException("config");
        }
        
        if (files == null) {
            throw new IllegalArgumentException("files");
        }
        
        // New up a command that's everything we need except the files to process
        // We'll use this as our reference for chunking up files
        File tslintOutputFile = this.tempFolder.newFile();
        String tslintOutputFilePath = tslintOutputFile.getAbsolutePath();
        
        LOG.debug("Using a temporary path for TsLint output: " + tslintOutputFilePath);
        
        int baseCommandLength = getBaseCommand(config, tslintOutputFilePath).toCommandLine().length();
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
        
        StringStreamConsumer stdOutConsumer = new StringStreamConsumer();
        StringStreamConsumer stdErrConsumer = new StringStreamConsumer();
        
        List<String> toReturn = new ArrayList<String>();
        
        for (int i = 0; i < batches.size(); i++) {
            StringBuilder outputBuilder = new StringBuilder();

            List<String> thisBatch = batches.get(i);

            Command thisCommand = getBaseCommand(config, tslintOutputFilePath);

            for (int fileIndex = 0; fileIndex < thisBatch.size(); fileIndex++) {
                thisCommand.addArgument(thisBatch.get(fileIndex));
            }

            LOG.debug("Executing TsLint with command: " + thisCommand.toCommandLine());

            // Timeout is specified per file, not per batch (which can vary a lot)
            // so multiply it up
            this.createExecutor().execute(thisCommand, stdOutConsumer, stdErrConsumer, config.getTimeoutMs() * thisBatch.size());
            
            try {
                BufferedReader reader = this.getBufferedReaderForFile(tslintOutputFile);
                
                String str;
                while ((str = reader.readLine()) != null) {
                    outputBuilder.append(str);
                }
                
                reader.close();
                
                toReturn.add(outputBuilder.toString());
            }
            catch (IOException ex) {
                LOG.error("Failed to re-read TsLint output from " + tslintOutputFilePath, ex);
            }
        }

        return toReturn;
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
