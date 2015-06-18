package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.CommandException;
import org.sonar.api.utils.command.StreamConsumer;

import java.util.concurrent.*;

public class TsLintExecutorImpl implements TsLintExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);
	
	private StringBuilder stdOut;
	private StringBuilder stdErr;
	private String customRuleDirectory;

	public String execute(String pathToTsLint, String configFile, String file) {
		LOG.info("TsLint executing for " + file);
		Command command = Command.create("tslint");

		if (customRuleDirectory != null && customRuleDirectory.length() > 0) {
			command
					.addArguments(new String[]{"-f", file.trim(), "-c", configFile, "--format", "json",
					" --rules-dir", customRuleDirectory});
		} else {
			command
					.addArguments(new String[]{"-f", file.trim(), "-c", configFile, "--format", "json"});
		}

		command.setNewShell(true);

		this.stdOut = new StringBuilder();
		this.stdErr = new StringBuilder();
		
		StreamConsumer stdOutConsumer = new StreamConsumer() {			
			public void consumeLine(String line) {
				LOG.trace("TsLint Out: " + line);
				stdOut.append(line + "\n");
			}
		};
		
		StreamConsumer stdErrConsumer = new StreamConsumer() {
			public void consumeLine(String line) {
				LOG.error("TsLint Err: " + line);
				stdErr.append(line + "\n");
			}
		};
		
		CommandExecutor executor = CommandExecutor.create();
		int exitCode = executor.execute(command, stdOutConsumer, stdErrConsumer, 5000);
		
		return stdOut.toString();
	}

	@Override
	public void setCustomRulesDirectory(String customRulesDirectory) {
		this.customRuleDirectory = customRulesDirectory;
	}
}
