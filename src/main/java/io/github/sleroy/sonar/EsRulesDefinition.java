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
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.DebtRemediationFunctions;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
            "EsLint issues that are not yet known to the plugin",
            "No description for ESLint rule");
    private static final Logger LOG = LoggerFactory.getLogger(EsRulesDefinition.class);
    @SuppressWarnings("HardcodedFileSeparator")
    private static final String CORE_RULES_CONFIG_RESOURCE_PATH = "/eslint/eslint-rules.properties";

    private final Settings settings;
    private final List<EsLintRule> eslintCoreRules = new ArrayList<>(100);
    private final List<EsLintRule> eslintRules = new ArrayList<>(100);

    public EsRulesDefinition() {
        this(null);
    }

    public EsRulesDefinition(Settings settings) {

        this.settings = settings;

        this.loadCoreRules();
        this.loadCustomRules();
    }

    public static void loadRules(InputStream stream, List<EsLintRule> rulesCollection) {
        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error("Error while loading ESLint rules: {}", e.getMessage());
        }

        for (String propKey : properties.stringPropertyNames()) {

            if (propKey.contains("."))
                continue;

            String ruleEnabled = properties.getProperty(propKey);

            if (!"true".equals(ruleEnabled))
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
            tsRule.setHtmlDescription(ruleDescription);
            rulesCollection.add(tsRule);
        }

        rulesCollection.sort((final EsLintRule r1, final EsLintRule r2) -> r1.getKey().compareTo(r2.getKey()));
    }

    private static void createRule(NewRepository repository, EsLintRule tsRule) {
        NewRule sonarRule =
                repository
                        .createRule(tsRule.getKey())
                        .setName(tsRule.getName())
                        .setSeverity(tsRule.getSeverity())
                        .setHtmlDescription(tsRule.getHtmlDescription())
                        .setStatus(RuleStatus.READY).
                setTags(tsRule.getTagsAsArray());


        if (tsRule.isHasDebtRemediation()) {
            DebtRemediationFunction debtRemediationFn = null;
            DebtRemediationFunctions funcs = sonarRule.debtRemediationFunctions();

            switch (tsRule.getDebtRemediationFunction()) {
                case LINEAR:
                    debtRemediationFn = funcs.linear(tsRule.getDebtRemediationScalar());
                    break;

                case LINEAR_OFFSET:
                    debtRemediationFn = funcs.linearWithOffset(tsRule.getDebtRemediationScalar(), tsRule.getDebtRemediationOffset());
                    break;

                case CONSTANT_ISSUE:
                    debtRemediationFn = funcs.constantPerIssue(tsRule.getDebtRemediationScalar());
                    break;
            }

            sonarRule.setDebtRemediationFunction(debtRemediationFn);
        }

        RuleType type = null;

        if (tsRule.getDebtType() != null && RuleType.names().contains(tsRule.getDebtType())) {
            // Try and parse it as a new-style rule type (since 5.5 SQALE's been replaced
            // with something simpler, and there's really only three buckets)
            type = RuleType.valueOf(tsRule.getDebtType());
        }

        if (type == null) {
            type = RuleType.CODE_SMELL;
        }

        sonarRule.setType(type);
    }

    private void loadCoreRules() {
        InputStream coreRulesStream = EsRulesDefinition.class.getResourceAsStream(CORE_RULES_CONFIG_RESOURCE_PATH);
        loadRules(coreRulesStream, this.eslintCoreRules);
    }

    private void loadCustomRules() {
        if (this.settings == null)
            return;

        List<String> configKeys = this.settings.getKeysStartingWith(EsLintPlugin.SETTING_ES_RULE_CONFIGS);

        for (String cfgKey : configKeys) {
            if (!cfgKey.endsWith("config"))
                continue;

            String rulesConfig = this.settings.getString(cfgKey);
            InputStream rulesConfigStream = new ByteArrayInputStream(rulesConfig.getBytes(Charset.defaultCharset()));
            loadRules(rulesConfigStream, this.eslintRules);
        }
    }

    @Override
    public void define(Context context) {
        NewRepository repository =
                context
                        .createRepository(REPOSITORY_NAME, EsLintLanguage.LANGUAGE_KEY)
                        .setName("ESLint Analyzer");

        EsRulesDefinition.createRule(repository, ESLINT_UNKNOWN_RULE);

        // add the ESLint builtin core rules
        for (EsLintRule coreRule : this.eslintCoreRules) {
            EsRulesDefinition.createRule(repository, coreRule);
        }

        // add additional custom ESLint rules
        for (EsLintRule customRule : this.eslintRules) {
            EsRulesDefinition.createRule(repository, customRule);
        }

        repository.done();
    }

    public List<EsLintRule> getCoreRules() {
        return this.eslintCoreRules;
    }

    public List<EsLintRule> getRules() {
        return this.eslintRules;
    }
}
