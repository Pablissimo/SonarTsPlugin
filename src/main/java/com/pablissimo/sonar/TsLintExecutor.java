package com.pablissimo.sonar;

public interface TsLintExecutor {
    String execute(String pathToTsLint, String configFile, String file);
}
