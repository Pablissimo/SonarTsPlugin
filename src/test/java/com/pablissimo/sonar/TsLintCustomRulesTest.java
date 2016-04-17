package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TsLintCustomRulesTest {
    private Settings settings;
    private TsRulesDefinition rules_def;

    @Before
    public void setUp() throws Exception {
        this.settings = mock(Settings.class);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void parsesSingleCustomRules() throws IOException {
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_LINT_CUSTOM_RULES_CONFIG))
            .thenReturn(
                "custom-rule-1=true\n" +
                "custom-rule-1.name=test rule #1\n" +
                "custom-rule-1.severity=MAJOR\n" +
                "custom-rule-1.description=#1 description\n" +
                "\n" +
                "custom-rule-2=true\n" +
                "custom-rule-2.name=test rule #2\n" +
                "custom-rule-2.severity=MINOR\n" +
                "custom-rule-2.description=#2 description\n" +
                "\n");

        this.rules_def = new TsRulesDefinition(this.settings);

        final int num_custom_rules = 2;

        assertEquals(this.rules_def.getCustomRules().size(), num_custom_rules);

        List<TsLintRule> custom_rules = this.rules_def.getCustomRules();

        if (custom_rules.size() == num_custom_rules) {
            TsLintRule rule_no_1 = custom_rules.get(0);
            TsLintRule rule_no_2 = custom_rules.get(1);

            assertEquals(rule_no_1.key, "custom-rule-1");
            assertEquals(rule_no_1.name, "test rule #1");
            assertEquals(rule_no_1.severity, "MAJOR");
            assertEquals(rule_no_1.html_description, "#1 description");

            assertEquals(rule_no_2.key, "custom-rule-2");
            assertEquals(rule_no_2.name, "test rule #2");
            assertEquals(rule_no_2.severity, "MINOR");
            assertEquals(rule_no_2.html_description, "#2 description");
        }
    }

    @Test
    public void parsesEmptyCustomRules() throws IOException {
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_LINT_CUSTOM_RULES_CONFIG))
            .thenReturn("#empty config\n");

        this.rules_def = new TsRulesDefinition(this.settings);

        final int num_custom_rules = 0;

        assertEquals(this.rules_def.getCustomRules().size(), num_custom_rules);
    }
}
