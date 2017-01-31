package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Settings;

public class EsLintExecutorConfig {
    public static final String CONFIG_FILENAME = ".eslintrc.json";
    public static final String ESLINT_FALLBACK_PATH = "node_modules/eslint/bin/eslint";

    private String pathToEsLint;
    private String configFile;
    private String rulesDir;

    private Integer timeoutMs;
    
    public static EsLintExecutorConfig fromSettings(Settings settings, SensorContext ctx, PathResolver resolver) {
        EsLintExecutorConfig toReturn = new EsLintExecutorConfig();

        toReturn.setPathToEsLint(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_PATH, ESLINT_FALLBACK_PATH));
        toReturn.setConfigFile(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_FILENAME));
        toReturn.setRulesDir(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_RULES_DIR, null));

        toReturn.setTimeoutMs(Math.max(5000, settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)));


        return toReturn;
    }


    public String getPathToEsLint() {
        return pathToEsLint;
    }

    public void setPathToEsLint(String pathToEsLint) {
        this.pathToEsLint = pathToEsLint;
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
