package com.pablissimo.sonar;

import org.sonar.api.*;

@Properties({
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_ENABLED,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_ENABLED_PREV,
        type = PropertyType.BOOLEAN,
        defaultValue = "true",
        name = "Enable tslint",
        description = "Specifies whether to run tslint on SonarQube analysis",
        project = true
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_PATH,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_PATH_PREV,
        defaultValue = "",
        name = "Path to tslint",
        description = "Path to installed copy of tslint to use to analyse the project",
        project = true,
        global = true
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_RULE_CONFIGS,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_RULE_CONFIGS_PREV,
        name = "TsLint Rule-Collections",
        description = "A collection of configurations for mapping tslint rules to Sonar rules",
        project = false,
        global = true,
        fields = {
            @PropertyField(
                key = "name",
                name = "Rule collection name",
                type = PropertyType.STRING
            ),
            @PropertyField(
                key = "config",
                name = "Rule configuration (see documentation)",
                type = PropertyType.TEXT,
                indicativeSize = 120
            )
        }
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES,
        deprecatedKey = TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES_PREV,
        type = PropertyType.BOOLEAN,
        defaultValue = "true",
        name = "Exclude .d.ts files",
        description = "Exclude .d.ts files from analysis",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_LCOV_REPORT_PATH,
        deprecatedKey = TypeScriptPlugin.SETTING_LCOV_REPORT_PATH_PREV,
        type = PropertyType.STRING,
        name = "LCOV report path",
        description = "Path to the LCOV report for code coverage, if one is available",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE,
        deprecatedKey = TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE_PREV,
        defaultValue = "false",
        type = PropertyType.BOOLEAN,
        name = "Force zero coverage",
        description = "Forces coverage of all source file to be set to 0% when no coverage report is provided",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH_PREV,
        defaultValue = "tslint.json",
        type = PropertyType.STRING,
        name = "Path to tslint.json rule configuration file",
        description = "Path to the file that configures the rules to be included in analysis",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR_PREV,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Custom rules dir",
        description = "Path to any custom rules directory to be supplied to tslint",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT_PREV,
        defaultValue = "60000",
        type = PropertyType.INTEGER,
        name = "Max TsLint wait time (milliseconds) per analysed file",
        description = "Maximum time to wait for tslint execution to finish before aborting (in milliseconds) per file sent to tslint",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_TYPECHECK,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_TYPECHECK_PREV,
        defaultValue = "false",
        type = PropertyType.BOOLEAN,
        name = "Forces tslint to run a type-check",
        description = "Equivalent to --type-check tslint argument - requires " + TypeScriptPlugin.SETTING_TS_LINT_PROJECT_PATH + " to also be set",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_PROJECT_PATH,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_PROJECT_PATH_PREV,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Path to tsconfig.json file, if required",
        description = "Required if " + TypeScriptPlugin.SETTING_TS_LINT_TYPECHECK + " setting specified, the path to the tsconfig.json file that describes the files to lint and build",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_OUTPUT_PATH,
        deprecatedKey = TypeScriptPlugin.SETTING_TS_LINT_OUTPUT_PATH_PREV,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Path to tslint JSON output file",
        description = "If set, the contents of this file will parsed for linting issues rather than the plugin running tslint itself",
        project = true,
        global = false
    ),
    @Property(
        key = TypeScriptPlugin.SETTING_TS_LINT_DISALLOW_CUSTOM_RULES,
        defaultValue = "false",
        type = PropertyType.BOOLEAN,
        name = "Disallow the usage of custom rules",
        description = "If set to true, custom rules will no longer be used for analysis",
        project = false,
        global = true
    )
})
public class TypeScriptPlugin implements Plugin {
    // Deprecated settings
    public static final String SETTING_EXCLUDE_TYPE_DEFINITION_FILES_PREV = "sonar.ts.excludetypedefinitionfiles";
    public static final String SETTING_FORCE_ZERO_COVERAGE_PREV = "sonar.ts.forceZeroCoverage";
    public static final String SETTING_IGNORE_NOT_FOUND_PREV = "sonar.ts.ignoreNotFound";
    public static final String SETTING_TS_LINT_ENABLED_PREV = "sonar.ts.tslintenabled";
    public static final String SETTING_TS_LINT_PATH_PREV = "sonar.ts.tslintpath";
    public static final String SETTING_TS_LINT_CONFIG_PATH_PREV = "sonar.ts.tslintconfigpath";
    public static final String SETTING_TS_LINT_TIMEOUT_PREV = "sonar.ts.tslinttimeout";
    public static final String SETTING_TS_LINT_RULES_DIR_PREV = "sonar.ts.tslintrulesdir";
    public static final String SETTING_LCOV_REPORT_PATH_PREV = "sonar.ts.lcov.reportpath";
    public static final String SETTING_TS_RULE_CONFIGS_PREV = "sonar.ts.ruleconfigs";
    public static final String SETTING_TS_LINT_TYPECHECK_PREV = "sonar.ts.tslinttypecheck";
    public static final String SETTING_TS_LINT_PROJECT_PATH_PREV = "sonar.ts.tslintprojectpath";
    public static final String SETTING_TS_LINT_OUTPUT_PATH_PREV = "sonar.ts.tslintoutputpath";

    // Current settings
    public static final String SETTING_EXCLUDE_TYPE_DEFINITION_FILES = "sonar.ts.excludeTypeDefinitionFiles";

    public static final String SETTING_FORCE_ZERO_COVERAGE = "sonar.ts.coverage.forceZeroIfUnspecified";
    public static final String SETTING_IGNORE_NOT_FOUND = "sonar.ts.coverage.ignoreNotFound";
    public static final String SETTING_LCOV_REPORT_PATH = "sonar.ts.coverage.lcovReportPath";

    public static final String SETTING_TS_LINT_ENABLED = "sonar.ts.tslint.enabled";
    public static final String SETTING_TS_LINT_PATH = "sonar.ts.tslint.path";
    public static final String SETTING_TS_LINT_NODE_PATH = "sonar.ts.tslint.nodePath";
    public static final String SETTING_TS_LINT_CONFIG_PATH = "sonar.ts.tslint.configPath";
    public static final String SETTING_TS_LINT_TIMEOUT = "sonar.ts.tslint.timeout";
    public static final String SETTING_TS_LINT_RULES_DIR = "sonar.ts.tslint.rulesDir";
    public static final String SETTING_TS_RULE_CONFIGS = "sonar.ts.tslint.ruleConfigs";
    public static final String SETTING_TS_LINT_TYPECHECK = "sonar.ts.tslint.typeCheck";
    public static final String SETTING_TS_LINT_PROJECT_PATH = "sonar.ts.tslint.projectPath";
    public static final String SETTING_TS_LINT_OUTPUT_PATH = "sonar.ts.tslint.outputPath";
    public static final String SETTING_TS_LINT_DISALLOW_CUSTOM_RULES = "sonar.ts.disallowcustomrules";


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
