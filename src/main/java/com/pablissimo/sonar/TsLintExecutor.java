package com.pablissimo.sonar;

public interface TsLintExecutor {
    String execute(String pathToTsLint, String configFile, String rulesDir, String file, Integer timeoutMs);
}
