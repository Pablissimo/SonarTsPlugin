package io.github.sleroy.sonar;

import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyField;
import org.sonar.api.PropertyType;

@Properties({
    @Property(
        key = EsLintPlugin.SETTING_ES_LINT_ENABLED,
        type = PropertyType.BOOLEAN,
        defaultValue = "true",
        name = "Enable ESLint",
        description = "Run ESLint on SonarQube analysis",
        project = true
    ),
    @Property(
        key = EsLintPlugin.SETTING_ES_LINT_PATH,
        defaultValue = "",
        name = "Path to ESLint",
        description = "Path to installed Node ESLint",
        project = true,
        global = true
    ),
    @Property(
        key = EsLintPlugin.SETTING_ES_RULE_CONFIGS,
        name = "ESLint Rule-Collections",
        description = "A collection of configurations for mapping ESLint rules to Sonar rules",
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
        key = EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH,
            defaultValue = ".eslintrc.js",
        type = PropertyType.STRING,
            name = "Path to eslint.json rule configuration file",
        description = "Path to the file that configures the rules to be included in analysis",
        project = true,
        global = false
    ),
    @Property(
        key = EsLintPlugin.SETTING_ES_LINT_RULES_DIR,
        defaultValue = "",
        type = PropertyType.STRING,
        name = "Custom rules dir",
        description = "Path to any custom rules directory to be supplied to ESLint",
        project = true,
        global = false
    ),
    @Property(
        key = EsLintPlugin.SETTING_ES_LINT_TIMEOUT,
        defaultValue = "60000",
        type = PropertyType.INTEGER,
        name = "Max ESLint wait time (milliseconds)",
        description = "Maximum time to wait for ESLint execution to finish before aborting (in milliseconds)",
        project = true,
        global = false
    )
})
/**
 * Main class to declare the EsLint Plugin
 */
public class EsLintPlugin implements Plugin {
    public static final String SETTING_IGNORE_NOT_FOUND = "sonar.eslint.ignoreNotFound";
    public static final String SETTING_ES_LINT_ENABLED = "sonar.eslint.eslintenabled";
    public static final String SETTING_ES_LINT_PATH = "sonar.eslint.eslintpath";
    public static final String SETTING_ES_LINT_CONFIG_PATH = "sonar.eslint.eslintconfigpath";
    public static final String SETTING_ES_LINT_TIMEOUT = "sonar.eslint.eslinttimeout";
    public static final String SETTING_ES_LINT_RULES_DIR = "sonar.eslint.eslintrulesdir";
    public static final String SETTING_ES_RULE_CONFIGS = "sonar.eslint.ruleconfigs";


    @Override
    public void define(Context ctx) {
        // Core components - the actual sensors doing the work or configuring
        // the plugin
        ctx
            .addExtension(EsLintRuleProfile.class)
            .addExtension(EsLintLanguage.class)
            .addExtension(EsLintSensor.class)

            .addExtension(EsRulesDefinition.class);
        
        // Additional services to be DI'd into the above
        ctx.addExtension(PathResolverImpl.class);
        ctx.addExtension(EsLintExecutorImpl.class);
        ctx.addExtension(EsLintParserImpl.class);

    }
}
