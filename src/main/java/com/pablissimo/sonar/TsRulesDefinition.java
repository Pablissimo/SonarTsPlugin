package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class TsRulesDefinition implements RulesDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);

    public static final String REPOSITORY_NAME = "tslint";

    public static final String DEFAULT_RULE_SEVERITY = Severity.defaultSeverity();
    public static final String DEFAULT_RULE_DESCRIPTION = "No description provided for this TsLint rule";
    public static final String DEFAULT_RULE_DEBT_SCALAR = "0min";
    public static final String DEFAULT_RULE_DEBT_OFFSET = "0min";
    public static final String DEFAULT_RULE_DEBT_TYPE = RuleType.CODE_SMELL.name();

    private static final String CORE_RULES_CONFIG_RESOURCE_PATH = "/tslint/tslint-rules.properties";

    /** The SonarQube rule that will contain all unknown TsLint issues. */
    public static final TsLintRule TSLINT_UNKNOWN_RULE = new TsLintRule(
        "tslint-issue",
        Severity.MAJOR,
        "tslint issues that are not yet known to the plugin",
        "No description for TsLint rule");

    private List<TsLintRule> tslintCoreRules = new ArrayList<>();
    private List<TsLintRule> tslintRules = new ArrayList<>();

    private final Settings settings;

    public TsRulesDefinition() {
        this(null);
    }

    public TsRulesDefinition(Settings settings) {

        this.settings = settings;

        loadCoreRules();
        loadCustomRules();
    }

    private void loadCoreRules() {
        InputStream coreRulesStream = TsRulesDefinition.class.getResourceAsStream(CORE_RULES_CONFIG_RESOURCE_PATH);
        loadRules(coreRulesStream, tslintCoreRules);
    }

    private void loadCustomRules() {
        if (this.settings == null)
            return;

        if (settings.getBoolean(TypeScriptPlugin.SETTING_TS_LINT_DISALLOW_CUSTOM_RULES)) {
            LOG.info("Usage of custom rules is inhibited");
            return;
        }

        List<String> configKeys = settings.getKeysStartingWith(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS);

        for (String cfgKey : configKeys) {
            if (!cfgKey.endsWith("config")) {
                continue;
            }

            String rulesConfig = settings.getString(cfgKey);
            if (rulesConfig != null) {
                InputStream rulesConfigStream = new ByteArrayInputStream(rulesConfig.getBytes(Charset.defaultCharset()));
                loadRules(rulesConfigStream, tslintRules);
            }
        }
    }

    public static void loadRules(InputStream stream, List<TsLintRule> rulesCollection) {
        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error("Error while loading TsLint rules", e);
        }

        for(String propKey : properties.stringPropertyNames()) {
            if (propKey.contains(".")) {
                continue;
            }

            String ruleEnabled = properties.getProperty(propKey);

            if (!"true".equals(ruleEnabled)) {
                continue;
            }

            String ruleId = propKey;
            String ruleName = properties.getProperty(propKey + ".name", ruleId.replace("-", " "));
            String ruleSeverity = properties.getProperty(propKey + ".severity", DEFAULT_RULE_SEVERITY);
            String ruleDescription = properties.getProperty(propKey + ".description", DEFAULT_RULE_DESCRIPTION);

            String debtRemediationFunction = properties.getProperty(propKey + ".debtFunc", null);
            String debtRemediationScalar = properties.getProperty(propKey + ".debtScalar", DEFAULT_RULE_DEBT_SCALAR);
            String debtRemediationOffset = properties.getProperty(propKey + ".debtOffset", DEFAULT_RULE_DEBT_OFFSET);
            String debtType = properties.getProperty(propKey + ".debtType", DEFAULT_RULE_DEBT_TYPE);

            TsLintRule tsRule = null;

            // try to apply the specified debt remediation function
            if (debtRemediationFunction != null) {
                DebtRemediationFunction.Type debtRemediationFunctionEnum = DebtRemediationFunction.Type.valueOf(debtRemediationFunction);

                tsRule = new TsLintRule(
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
                tsRule = new TsLintRule(
                    ruleId,
                    ruleSeverity,
                    ruleName,
                    ruleDescription
                );
            }

            rulesCollection.add(tsRule);
        }

        Collections.sort(rulesCollection, (TsLintRule r1, TsLintRule r2) -> r1.key.compareTo(r2.key));
    }

    private void createRule(NewRepository repository, TsLintRule tsRule) {
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

    @Override
    public void define(Context context) {
        NewRepository repository =
                context
                .createRepository(REPOSITORY_NAME, TypeScriptLanguage.LANGUAGE_KEY)
                .setName("TsLint Analyzer");

        createRule(repository, TSLINT_UNKNOWN_RULE);

        // add the TsLint builtin core rules
        for (TsLintRule coreRule : tslintCoreRules) {
            createRule(repository, coreRule);
        }

        // add additional custom TsLint rules
        for (TsLintRule customRule : tslintRules) {
            createRule(repository, customRule);
        }

        repository.done();
    }

    public List<TsLintRule> getCoreRules() {
        return tslintCoreRules;
    }

    public List<TsLintRule> getRules() {
        return tslintRules;
    }
}
