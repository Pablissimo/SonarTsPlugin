package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.utils.command.Command;
import org.sonar.api.utils.command.CommandExecutor;
import org.sonar.api.utils.command.StreamConsumer;

public class TsLintExecutor {
	private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutor.class);
	
	private StringBuilder stdOut;
	private StringBuilder stdErr;
	
	public String execute(String executable, String configFile, String file) {
		Command command = Command.create(executable);
		
		command
			.addArgument("--format json")
			.addArgument("-f " + file);
		
		this.stdOut = new StringBuilder();
		this.stdErr = new StringBuilder();
		
		StreamConsumer stdOutConsumer = new StreamConsumer() {			
			public void consumeLine(String line) {
				LOG.info(line);
				stdOut.append(line + "\n");
			}
		};
		
		StreamConsumer stdErrConsumer = new StreamConsumer() {
			public void consumeLine(String line) {
				LOG.error(line);
				stdErr.append(line + "\n");
			}
		};
		
		CommandExecutor executor = CommandExecutor.create();
		executor.execute(command, stdOutConsumer, stdErrConsumer, 5000);
		
		return stdOut.toString();
	}
}
