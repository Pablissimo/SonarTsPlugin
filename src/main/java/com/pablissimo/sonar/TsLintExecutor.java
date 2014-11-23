package com.pablissimo.sonar;

import org.jfree.util.Log;
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
		LOG.info("TsLint executing for " + file);
		Command command = Command.create(executable);
		
		command
			.addArgument("\"C:\\Users\\Pabliissimo\\AppData\\Roaming\\npm\\node_modules\\tslint\\bin\\tslint\" --format json -f \"" + file.trim() + "\"");
		
		this.stdOut = new StringBuilder();
		this.stdErr = new StringBuilder();
		
		StreamConsumer stdOutConsumer = new StreamConsumer() {			
			public void consumeLine(String line) {
				LOG.info("TsLint Out: " + line);
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
		
		LOG.info("Exit code " + exitCode);
		try {
			LOG.info("CLI " + command.toCommandLine());
		}
		catch (Exception e) {
			LOG.error(e.toString());
		}
		
		LOG.debug("Got " + stdOut.toString());
		
		return stdOut.toString();
	}
}
