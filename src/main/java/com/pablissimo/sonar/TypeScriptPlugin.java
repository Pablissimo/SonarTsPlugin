package com.pablissimo.sonar;

import org.sonar.api.*;

@Properties({
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_PATH,
        defaultValue = "",
        name = "Path to TSLint",
        description = "Path to installed Node TSLint",
        project = true,
        global = true
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_RULE_CONFIGS,
        name = "TsLint Rule-Collections",
        description = "A collection of configurations for mapping TsLint rules to Sonar rules",
        project = false,
        global = true,
        fields = {
            @PropertyField(
                key = "name",
                name = "rule collection name",
                type = PropertyType.STRING
            ),
            @PropertyField(
                key = "config",
                name = "rule configs & parameters",
                type = PropertyType.TEXT,
                indicativeSize = 120
            )
        }
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES,
        type = PropertyType.BOOLEAN,
        defaultValue = "true",
        name = "Exclude .d.ts files",
        description = "Exclude .d.ts files from analysis",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_LCOV_REPORT_PATH,
        type = PropertyType.STRING,
        name = "LCOV report path",
        description = "LCOV report path",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE,
        defaultValue = "false",
        type = PropertyType.BOOLEAN,
        name = "Force 0 coverage",
        description = "Force coverage to be set to 0 when no report is provided.",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH,
        defaultValue = "tslint.json",
        type = PropertyType.STRING,
        name = "Path to tslint.json rule configuration file",
        description = "Path to the file that configures the rules to be included in analysis",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Custom rules dir",
        description = "Path to any custom rules directory to be supplied to TsLint",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT,
        defaultValue = "60000",
        type = PropertyType.INTEGER,
        name = "Max TsLint wait time (milliseconds)",
        description = "Maximum time to wait for TsLint execution to finish before aborting (in milliseconds)",
        project = true,
        global = false
    )/*,
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_TYPECHECK,
        defaultValue = "false",
        type = PropertyType.BOOLEAN,
        name = "Forces tslint to run a type-check",
        description = "Equivalent to --type-check tslint argument - requires tslintconfigpath also set",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_TSCONFIG_PATH,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Path to tsconfig.json file, if required",
        description = "Required if tslinttypecheck parameter specified, the path to the tsconfig.json file that describes the files to lint and build",
        project = true,
        global = false
    )*/
})
public class TypeScriptPlugin implements Plugin {
    public static final String SETTING_EXCLUDE_TYPE_DEFINITION_FILES = "sonar.ts.excludetypedefinitionfiles";
    public static final String SETTING_FORCE_ZERO_COVERAGE = "sonar.ts.forceZeroCoverage";
    public static final String SETTING_IGNORE_NOT_FOUND = "sonar.ts.ignoreNotFound";
    public static final String SETTING_TS_LINT_PATH = "sonar.ts.tslintpath";
    public static final String SETTING_TS_LINT_CONFIG_PATH = "sonar.ts.tslintconfigpath";
    public static final String SETTING_TS_LINT_TIMEOUT = "sonar.ts.tslinttimeout";
    public static final String SETTING_TS_LINT_RULES_DIR = "sonar.ts.tslintrulesdir";
    public static final String SETTING_LCOV_REPORT_PATH = "sonar.ts.lcov.reportpath";
    public static final String SETTING_TS_RULE_CONFIGS = "sonar.ts.ruleconfigs";
    public static final String SETTING_TS_LINT_TYPECHECK = "sonar.ts.tslinttypecheck";
    public static final String SETTING_TS_LINT_TSCONFIG_PATH = "sonar.ts.tslinttsconfigpath";

    @Override
    public void define(Context ctx) {
        // Core components - the actual sensors doing the work or configuring
        // the plugin
        ctx
            .addExtension(TypeScriptRuleProfile.class)
            .addExtension(TypeScriptLanguage.class)
            .addExtension(TsLintSensor.class)
            .addExtension(CombinedCoverageSensor.class)
            .addExtension(TsRulesDefinition.class);
        
        // Additional services to be DI'd into the above
        ctx.addExtension(PathResolverImpl.class);
        ctx.addExtension(TsLintExecutorImpl.class);
        ctx.addExtension(TsLintParserImpl.class);
        ctx.addExtension(LOCSensorImpl.class);
        ctx.addExtension(TsCoverageSensorImpl.class);
    }
}
