package io.github.sleroy.sonar;

import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Settings;

public class EsLintExecutorConfig {
    public static final String CONFIG_FILENAME = "tslint.json";
    public static final String TSLINT_FALLBACK_PATH = "node_modules/tslint/bin/tslint";
    
    private String pathToTsLint;
    private String configFile;
    private String rulesDir;
    private String pathToTsConfig;
    private boolean shouldPerformTypeCheck;
    
    private Integer timeoutMs;
    
    public static EsLintExecutorConfig fromSettings(Settings settings, SensorContext ctx, PathResolver resolver) {
        EsLintExecutorConfig toReturn = new EsLintExecutorConfig();
        
        toReturn.setPathToTsLint(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_PATH, TSLINT_FALLBACK_PATH));
        toReturn.setConfigFile(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_FILENAME));
        toReturn.setRulesDir(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_RULES_DIR, null));
        toReturn.setPathToTsConfig(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_PROJECT_PATH, null));
        
        toReturn.setTimeoutMs(Math.max(5000, settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)));
        toReturn.setShouldPerformTypeCheck(settings.getBoolean(EsLintPlugin.SETTING_TS_LINT_TYPECHECK));
        
        return toReturn;
    }
    
    public Boolean useTsConfigInsteadOfFileList() {
        return this.pathToTsConfig != null && !this.pathToTsConfig.isEmpty();
    }

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

    public String getPathToTsConfig() {
        return pathToTsConfig;
    }

    public void setPathToTsConfig(String pathToTsConfig) {
        this.pathToTsConfig = pathToTsConfig;
    }

    public boolean shouldPerformTypeCheck() {
        return this.shouldPerformTypeCheck;
    }

    public void setShouldPerformTypeCheck(boolean performTypeCheck) {
        this.shouldPerformTypeCheck = performTypeCheck;
    }
}
