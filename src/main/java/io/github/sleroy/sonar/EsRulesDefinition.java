package io.github.sleroy.sonar;

import io.github.sleroy.sonar.model.EsLintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class EsRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_NAME = "eslint";
    public static final String DEFAULT_RULE_SEVERITY = Severity.defaultSeverity();
    public static final String DEFAULT_RULE_DESCRIPTION = "No description provided for this ESLint rule";
    public static final String DEFAULT_RULE_DEBT_SCALAR = "0min";
    public static final String DEFAULT_RULE_DEBT_OFFSET = "0min";
    public static final String DEFAULT_RULE_DEBT_TYPE = RuleType.CODE_SMELL.name();
    /**
     * The SonarQube rule that will contain all unknown ESLint issues.
     */
    public static final EsLintRule ESLINT_UNKNOWN_RULE = new EsLintRule(
            "eslint-issue",
        Severity.MAJOR,
            "eslint issues that are not yet known to the plugin",
            "No description for ESLint rule");
    private static final Logger LOG = LoggerFactory.getLogger(EsRulesDefinition.class);
    private static final String CORE_RULES_CONFIG_RESOURCE_PATH = "/eslint/eslint-rules.properties";
    private final Settings settings;
    private List<EsLintRule> eslintCoreRules = new ArrayList<>();
    private List<EsLintRule> eslintRules = new ArrayList<>();

    public EsRulesDefinition() {
        this(null);
    }

    public EsRulesDefinition(Settings settings) {

        this.settings = settings;

        loadCoreRules();
        loadCustomRules();
    }

    public static void loadRules(InputStream stream, List<EsLintRule> rulesCollection) {
        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error("Error while loading ESLint rules: " + e.getMessage());
        }

        for(String propKey : properties.stringPropertyNames()) {

            if (propKey.contains("."))
                continue;

            String ruleEnabled = properties.getProperty(propKey);

            if (!ruleEnabled.equals("true"))
                continue;

            String ruleId = propKey;
            String ruleName = properties.getProperty(propKey + ".name", ruleId.replace("-", " "));
            String ruleSeverity = properties.getProperty(propKey + ".severity", DEFAULT_RULE_SEVERITY);
            String ruleDescription = properties.getProperty(propKey + ".description", DEFAULT_RULE_DESCRIPTION);

            String debtRemediationFunction = properties.getProperty(propKey + ".debtFunc", null);
            String debtRemediationScalar = properties.getProperty(propKey + ".debtScalar", DEFAULT_RULE_DEBT_SCALAR);
            String debtRemediationOffset = properties.getProperty(propKey + ".debtOffset", DEFAULT_RULE_DEBT_OFFSET);
            String debtType = properties.getProperty(propKey + ".debtType", DEFAULT_RULE_DEBT_TYPE);

            EsLintRule tsRule = null;

            // try to apply the specified debt remediation function
            if (debtRemediationFunction != null) {
                DebtRemediationFunction.Type debtRemediationFunctionEnum = DebtRemediationFunction.Type.valueOf(debtRemediationFunction);

                tsRule = new EsLintRule(
                    ruleId,
                    ruleSeverity,
                    ruleName,
                    ruleDescription,
                    debtRemediationFunctionEnum,
                    debtRemediationScalar,
                    debtRemediationOffset,
                    debtType
                );
            }

            // no debt remediation function specified
            if (tsRule == null) {
                tsRule = new EsLintRule(
                    ruleId,
                    ruleSeverity,
                    ruleName,
                    ruleDescription
                );
            }

            rulesCollection.add(tsRule);
        }

        Collections.sort(rulesCollection, new Comparator<EsLintRule>() {
            @Override
            public int compare(EsLintRule r1, EsLintRule r2) {
                return r1.key.compareTo(r2.key);
            }
        });
    }

    private void loadCoreRules() {
        InputStream coreRulesStream = EsRulesDefinition.class.getResourceAsStream(CORE_RULES_CONFIG_RESOURCE_PATH);
        loadRules(coreRulesStream, eslintCoreRules);
    }

    private void loadCustomRules() {
        if (this.settings == null)
            return;

        List<String> configKeys = settings.getKeysStartingWith(EsLintPlugin.SETTING_ES_RULE_CONFIGS);

        for (String cfgKey : configKeys) {
            if (!cfgKey.endsWith("config"))
                continue;

            String rulesConfig = settings.getString(cfgKey);
            InputStream rulesConfigStream = new ByteArrayInputStream(rulesConfig.getBytes(Charset.defaultCharset()));
            loadRules(rulesConfigStream, eslintRules);
        }
    }

    private void createRule(NewRepository repository, EsLintRule tsRule) {
        NewRule sonarRule = 
                    repository
                    .createRule(tsRule.key)
                    .setName(tsRule.name)
                    .setSeverity(tsRule.severity)
                    .setHtmlDescription(tsRule.htmlDescription)
                    .setStatus(RuleStatus.READY);

        if (tsRule.hasDebtRemediation) {
            DebtRemediationFunction debtRemediationFn = null;
            DebtRemediationFunctions funcs = sonarRule.debtRemediationFunctions();

            switch (tsRule.debtRemediationFunction)
            {
                case LINEAR:
                    debtRemediationFn = funcs.linear(tsRule.debtRemediationScalar);
                    break;

                case LINEAR_OFFSET:
                    debtRemediationFn = funcs.linearWithOffset(tsRule.debtRemediationScalar, tsRule.debtRemediationOffset);
                    break;

                case CONSTANT_ISSUE:
                    debtRemediationFn = funcs.constantPerIssue(tsRule.debtRemediationScalar);
                    break;
            }

            sonarRule.setDebtRemediationFunction(debtRemediationFn);
        }
        
        RuleType type = null;

        if (tsRule.debtType != null && RuleType.names().contains(tsRule.debtType)) {
            // Try and parse it as a new-style rule type (since 5.5 SQALE's been replaced
            // with something simpler, and there's really only three buckets)
            type = RuleType.valueOf(tsRule.debtType);
        }
        
        if (type == null) {
            type = RuleType.CODE_SMELL;
        }
                
        sonarRule.setType(type);
    }

    public void define(Context context) {
        NewRepository repository =
                context
                .createRepository(REPOSITORY_NAME, EsLintLanguage.LANGUAGE_KEY)
                        .setName("ESLint Analyzer");

        createRule(repository, ESLINT_UNKNOWN_RULE);

        // add the ESLint builtin core rules
        for (EsLintRule coreRule : eslintCoreRules) {
            createRule(repository, coreRule);
        }

        // add additional custom ESLint rules
        for (EsLintRule customRule : eslintRules) {
            createRule(repository, customRule);
        }

        repository.done();
    }

    public List<EsLintRule> getCoreRules() {
        return eslintCoreRules;
    }

    public List<EsLintRule> getRules() {
        return eslintRules;
    }
}
