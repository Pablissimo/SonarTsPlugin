package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class TsRulesDefinition implements RulesDefinition {
    private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);

    public static final String REPOSITORY_NAME = "tslint";

    public static TsLintRule TSLINT_UNKNOWN_RULE = new TsLintRule("tslint-issue", Severity.MAJOR, "tslint issues that are not yet known to the plugin", "HTML description to follow");

    public static List<TsLintRule> TSLINT_CORE_RULES = new ArrayList<>();

    static
    {
        if (TSLINT_CORE_RULES.size() == 0) {
            InputStream tslint_builtin_rules = TsRulesDefinition.class.getResourceAsStream("/tslint/tslint-rules.properties");

            Properties properties = new Properties();
            try {
                properties.load(tslint_builtin_rules);
            } catch (IOException e) {
                LOG.error("Error while loading tslint.rules ... " + e.getMessage());
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
                String rule_description = properties.getProperty(prop_key + ".description", "HTML description to follow");

                TSLINT_CORE_RULES.add(new TsLintRule(
                    rule_id,
                    rule_severity,
                    rule_name,
                    rule_description
                ));
            }

            TSLINT_CORE_RULES.sort(new Comparator<TsLintRule>() {
                @Override
                public int compare(TsLintRule o1, TsLintRule o2) {
                    return o1.key.compareTo(o2.key);
                }
            });
        }
    }

    private void applyRule(NewRepository repository, TsLintRule rule) {
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

        applyRule(repository, TSLINT_UNKNOWN_RULE);

        for (TsLintRule builtin : TSLINT_CORE_RULES) {
            applyRule(repository, builtin);
        }

        repository.done();
    }
}
