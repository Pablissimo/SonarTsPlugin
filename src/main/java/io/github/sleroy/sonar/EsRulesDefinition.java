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

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class EsRulesDefinition implements RulesDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(EsRulesDefinition.class);

    public static final String REPOSITORY_NAME = "tslint";

    public static final String DEFAULT_RULE_SEVERITY = Severity.defaultSeverity();
    public static final String DEFAULT_RULE_DESCRIPTION = "No description provided for this TsLint rule";
    public static final String DEFAULT_RULE_DEBT_SCALAR = "0min";
    public static final String DEFAULT_RULE_DEBT_OFFSET = "0min";
    public static final String DEFAULT_RULE_DEBT_TYPE = RuleType.CODE_SMELL.name();

    private static final String CORE_RULES_CONFIG_RESOURCE_PATH = "/tslint/tslint-rules.properties";

    /** The SonarQube rule that will contain all unknown TsLint issues. */
    public static final EsLintRule ESLINT_UNKNOWN_RULE = new EsLintRule(
        "tslint-issue",
        Severity.MAJOR,
        "tslint issues that are not yet known to the plugin",
        "No description for TsLint rule");

    private List<EsLintRule> tslintCoreRules = new ArrayList<>();
    private List<EsLintRule> tslintRules = new ArrayList<>();

    private final Settings settings;

    public EsRulesDefinition() {
        this(null);
    }

    public EsRulesDefinition(Settings settings) {

        this.settings = settings;

        loadCoreRules();
        loadCustomRules();
    }

    private void loadCoreRules() {
        InputStream coreRulesStream = EsRulesDefinition.class.getResourceAsStream(CORE_RULES_CONFIG_RESOURCE_PATH);
        loadRules(coreRulesStream, tslintCoreRules);
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
            loadRules(rulesConfigStream, tslintRules);
        }
    }

    public static void loadRules(InputStream stream, List<EsLintRule> rulesCollection) {
        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error("Error while loading TsLint rules: " + e.getMessage());
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
                .setName("TsLint Analyzer");

        createRule(repository, ESLINT_UNKNOWN_RULE);

        // add the TsLint builtin core rules
        for (EsLintRule coreRule : tslintCoreRules) {
            createRule(repository, coreRule);
        }

        // add additional custom TsLint rules
        for (EsLintRule customRule : tslintRules) {
            createRule(repository, customRule);
        }

        repository.done();
    }

    public List<EsLintRule> getCoreRules() {
        return tslintCoreRules;
    }

    public List<EsLintRule> getRules() {
        return tslintRules;
    }
}
