package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.config.Settings;

import java.io.File;
import java.util.Optional;
import java.util.stream.Stream;

public class EsLintExecutorConfig {
    public static final String ESLINT_FALLBACK_PATH = "node_modules" + File.separatorChar + "eslint" + File.separatorChar + "bin" + File.separatorChar + "eslint.js";
    public static final String CONFIG_JS_FILENAME = ".eslintrc.js";
    public static final String CONFIG_JSON_FILENAME = ".eslintrc.json";
    public static final String CONFIG_YAML_FILENAME = ".eslintrc.yml";
    public static final String CONFIG_YAML2_FILENAME = ".eslintrc.yaml";
    public static final String CONFIG_FILENAME = ".eslintrc";
    public static final int MAX_TIMEOUT = 5000;

    private String pathToEsLint;
    private String configFile;
    private String rulesDir;

    private Integer timeoutMs;

    public static EsLintExecutorConfig fromSettings(Settings settings, SensorContext ctx, PathResolver resolver) {
        EsLintExecutorConfig toReturn = new EsLintExecutorConfig();

        resolver
                .getPathFromSetting(ctx, EsLintPlugin.SETTING_ES_LINT_PATH, EsLintExecutorConfig.ESLINT_FALLBACK_PATH)
                .ifPresent(f -> toReturn.pathToEsLint = f);


        Stream.of(
                resolver.getPathFromSetting(ctx, EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH),
                resolver.getAbsolutePath(ctx, EsLintExecutorConfig.CONFIG_FILENAME),
                resolver.getAbsolutePath(ctx, EsLintExecutorConfig.CONFIG_JS_FILENAME),
                resolver.getAbsolutePath(ctx, EsLintExecutorConfig.CONFIG_JSON_FILENAME),
                resolver.getAbsolutePath(ctx, EsLintExecutorConfig.CONFIG_YAML2_FILENAME),
                resolver.getAbsolutePath(ctx, EsLintExecutorConfig.CONFIG_YAML_FILENAME)
        )
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst()
                .ifPresent(f -> toReturn.configFile = f);


        resolver.getPathFromSetting(ctx, EsLintPlugin.SETTING_ES_LINT_RULES_DIR, null)
                .ifPresent(f -> toReturn.rulesDir = f);

        toReturn.timeoutMs = Math.max(EsLintExecutorConfig.MAX_TIMEOUT, settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT));


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
