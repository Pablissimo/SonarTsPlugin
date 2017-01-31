package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Settings;

public class EsLintExecutorConfig {
    public static final String ESLINT_FALLBACK_PATH = "node_modules/eslint/bin/eslint.js";
    public static final String CONFIG_JS_FILENAME = ".eslintrc.js";
    public static final String CONFIG_JSON_FILENAME = ".eslintrc.json";
    public static final String CONFIG_YAML_FILENAME = ".eslintrc.yml";
    public static final String CONFIG_YAML2_FILENAME = ".eslintrc.yaml";
    public static final String CONFIG_FILENAME = ".eslintrc";

    private String pathToEsLint;
    private String configFile;
    private String rulesDir;

    private Integer timeoutMs;

    public static EsLintExecutorConfig fromSettings(Settings settings, SensorContext ctx, PathResolver resolver) {
        EsLintExecutorConfig toReturn = new EsLintExecutorConfig();

        toReturn.setPathToEsLint(resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_PATH, ESLINT_FALLBACK_PATH));
        // Try JS Config
        String defaultConfigFile = resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_JS_FILENAME);
        // Try json config
        if (defaultConfigFile == null) {
            defaultConfigFile = resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_JSON_FILENAME);
        }
        // Try yaml config
        if (defaultConfigFile == null) {
            defaultConfigFile = resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_YAML_FILENAME);
            if (defaultConfigFile == null) {
                defaultConfigFile = resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_YAML2_FILENAME);
            }
        }
        // Try without extension config
        if (defaultConfigFile == null) {
            defaultConfigFile = resolver.getPath(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, CONFIG_FILENAME);
        }
        toReturn.setConfigFile(defaultConfigFile);
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
