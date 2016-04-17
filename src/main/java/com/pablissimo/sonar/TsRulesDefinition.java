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

    private List<TsLintRule> tslint_core_rules = new ArrayList<>();
    private List<TsLintRule> tslint_custom_rules = new ArrayList<>();

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
        InputStream core_rules_stream = TsRulesDefinition.class.getResourceAsStream(CORE_RULES_CONFIG_RESOURCE_PATH);
        loadRules(core_rules_stream, tslint_core_rules);
    }

    private void loadCustomRules() {
        if (this.settings == null)
            return;

        String custom_rules_cfg = this.settings.getString(TypeScriptPlugin.SETTING_TS_LINT_CUSTOM_RULES_CONFIG);

        if (custom_rules_cfg != null) {
            InputStream custom_rules_stream = new ByteArrayInputStream(custom_rules_cfg.getBytes(Charset.defaultCharset()));
            loadRules(custom_rules_stream, tslint_custom_rules);
        }
    }

    public static void loadRules(InputStream stream, List<TsLintRule> rules_collection) {
        Properties properties = new Properties();

        try {
            properties.load(stream);
        } catch (IOException e) {
            LOG.error("Error while loading TsLint rules: " + e.getMessage());
        }

        for(String prop_key : properties.stringPropertyNames()) {

            if (prop_key.contains("."))
                continue;

            String rule_enabled = properties.getProperty(prop_key);

            if (!rule_enabled.equals("true"))
                continue;

            String rule_id = prop_key;
            String rule_severity = properties.getProperty(prop_key + ".severity", Severity.defaultSeverity());
            String rule_name = properties.getProperty(prop_key + ".name", "Unnamed TsLint rule");
            String rule_description = properties.getProperty(prop_key + ".description", "No description for TsLint rule");

            rules_collection.add(new TsLintRule(
                rule_id,
                rule_severity,
                rule_name,
                rule_description
            ));
        }

        Collections.sort(rules_collection, new Comparator<TsLintRule>() {
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
            .setHtmlDescription(rule.html_description)
            .setStatus(RuleStatus.READY);
    }

    public void define(Context context) {
        NewRepository repository =
                context
                .createRepository(REPOSITORY_NAME, TypeScriptLanguage.LANGUAGE_KEY)
                .setName("TsLint Analyzer");

        createRule(repository, TSLINT_UNKNOWN_RULE);

        // add the TsLint builtin core rules
        for (TsLintRule core_rule : tslint_core_rules) {
            createRule(repository, core_rule);
        }

        // add additional custom TsLint rules
        for (TsLintRule custom_rule : tslint_custom_rules) {
            createRule(repository, custom_rule);
        }

        repository.done();
    }

    public List<TsLintRule> getCoreRules() {
        return tslint_core_rules;
    }

    public List<TsLintRule> getCustomRules() {
        return tslint_custom_rules;
    }
}
