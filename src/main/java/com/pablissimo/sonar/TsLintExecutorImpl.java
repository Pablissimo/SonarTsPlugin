package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

public class TsLintExecutorImpl implements TsLintExecutor {	
	private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);
	
	private StringBuilder stdOut;
	private StringBuilder stdErr;
	
	public String execute(String pathToTsLint, String configFile, String file) {
		LOG.info("TsLint executing for " + file);
		Command command = Command.create("node");
		
		command
			.addArgument("\"" + pathToTsLint + "\" --config \"" + configFile + "\" --format json \"" + file.trim() + "\"");
			
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
}
