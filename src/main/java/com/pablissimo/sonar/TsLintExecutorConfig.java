package com.pablissimo.sonar;

public class TsLintExecutorConfig {
    private String pathToTsLint;
    private String configFile;
    private String rulesDir;
    private Integer timeoutMs;

    public String getPathToTsLint() {
        return pathToTsLint;
    }

    public void setPathToTsLint(String pathToTsLint) {
        this.pathToTsLint = pathToTsLint;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public String getRulesDir() {
        return rulesDir;
    }

    public void setRulesDir(String rulesDir) {
        this.rulesDir = rulesDir;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
