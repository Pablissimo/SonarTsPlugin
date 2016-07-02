package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class TsRulesDefinition implements RulesDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);

    public static final String REPOSITORY_NAME = "tslint";

    private static final String CORE_RULES_CONFIG_RESOURCE_PATH = "/tslint/tslint-rules.properties";

    /** The SonarQube rule that will contain all unknown TsLint issues. */
    public static final TsLintRule TSLINT_UNKNOWN_RULE = new TsLintRule(
        "tslint-issue",
        Severity.MAJOR,
        "tslint issues that are not yet known to the plugin",
        "No description for TsLint rule");

    private List<TsLintRule> tslintCoreRules = new ArrayList<>();
    private List<TsLintRule> tslintCustomRules = new ArrayList<>();

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

        String customRulesCfg = this.settings.getString(TypeScriptPlugin.SETTING_TS_LINT_CUSTOM_RULES_CONFIG);

        if (customRulesCfg != null) {
            InputStream customRulesStream = new ByteArrayInputStream(customRulesCfg.getBytes(Charset.defaultCharset()));
            loadRules(customRulesStream, tslintCustomRules);
        }
    }

    public static void loadRules(InputStream stream, List<TsLintRule> rulesCollection) {
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
            String ruleSeverity = properties.getProperty(propKey + ".severity", Severity.defaultSeverity());
            String ruleName = properties.getProperty(propKey + ".name", "Unnamed TsLint rule");
            String ruleDescription = properties.getProperty(propKey + ".description", "No description for TsLint rule");

            rulesCollection.add(new TsLintRule(
                ruleId,
                ruleSeverity,
                ruleName,
                ruleDescription
            ));
        }

        Collections.sort(rulesCollection, new Comparator<TsLintRule>() {
            @Override
            public int compare(TsLintRule r1, TsLintRule r2) {
                return r1.key.compareTo(r2.key);
            }
        });
    }

    private void createRule(NewRepository repository, TsLintRule rule) {
        repository
            .createRule(rule.key)
            .setName(rule.name)
            .setSeverity(rule.severity)
            .setHtmlDescription(rule.htmlDescription)
            .setStatus(RuleStatus.READY);
    }

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
        for (TsLintRule customRule : tslintCustomRules) {
            createRule(repository, customRule);
        }

        repository.done();
    }

    public List<TsLintRule> getCoreRules() {
        return tslintCoreRules;
    }

    public List<TsLintRule> getCustomRules() {
        return tslintCustomRules;
    }
}
