package io.github.sleroy.sonar;

import io.github.sleroy.sonar.model.EsLintRule;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction.Type;
import org.sonar.api.server.rule.RulesDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EsRulesDefinitionTest {

    Settings settings;
    EsRulesDefinition definition;
    RulesDefinition.Context context;

    @Before
    public void setUp() throws Exception {

        settings = mock(Settings.class);

        when(settings.getKeysStartingWith(EsLintPlugin.SETTING_ES_RULE_CONFIGS))
            .thenReturn(new ArrayList<String>() {{
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg1.name");
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg1.config");
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg2.name");
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg2.config");
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg3.name");
                this.add(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg3.config");
            }});

        // config with one disabled rule
        when(settings.getString(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg1.config"))
            .thenReturn(
                "custom-rule-1=false\n" +
                "custom-rule-1.name=test rule #1\n" +
                "custom-rule-1.severity=MAJOR\n" +
                "custom-rule-1.description=#1 description\n" +
                "\n"
            );

        // config with a basic rule (no debt settings)
        when(settings.getString(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg2.config"))
            .thenReturn(
                "custom-rule-2=true\n" +
                "custom-rule-2.name=test rule #2\n" +
                "custom-rule-2.severity=MINOR\n" +
                "custom-rule-2.description=#2 description\n" +
                "\n"
            );

        // config with a advanced rules (including debt settings)
        when(settings.getString(EsLintPlugin.SETTING_ES_RULE_CONFIGS + ".cfg3.config"))
            .thenReturn(
                "custom-rule-3=true\n" +
                "custom-rule-3.name=test rule #3\n" +
                "custom-rule-3.severity=INFO\n" +
                "custom-rule-3.description=#3 description\n" +
                        "custom-rule-3.debtFunc=" + Type.CONSTANT_ISSUE + "\n" +
                "custom-rule-3.debtScalar=15min\n" +
                "custom-rule-3.debtOffset=1min\n" +
                "custom-rule-3.debtType=INVALID_TYPE_GOES_HERE\n" +
                "\n" +
                "custom-rule-4=true\n" +
                "custom-rule-4.name=test rule #4\n" +
                "custom-rule-4.severity=MINOR\n" +
                "custom-rule-4.description=#4 description\n" +
                        "custom-rule-4.debtFunc=" + Type.LINEAR + "\n" +
                "custom-rule-4.debtScalar=5min\n" +
                "custom-rule-4.debtOffset=2h\n" +
                "custom-rule-4.debtType=" + RuleType.BUG.name() + "\n" +
                "\n" +
                "custom-rule-5=true\n" +
                "custom-rule-5.name=test rule #5\n" +
                "custom-rule-5.severity=MAJOR\n" +
                "custom-rule-5.description=#5 description\n" +
                        "custom-rule-5.debtFunc=" + Type.LINEAR_OFFSET + "\n" +
                "custom-rule-5.debtScalar=30min\n" +
                "custom-rule-5.debtOffset=15min\n" +
                "custom-rule-5.debtType=" + RuleType.VULNERABILITY.name() + "\n" +
                "\n"
            );

        definition = new EsRulesDefinition(settings);
        context = new RulesDefinition.Context();
        definition.define(this.context);
    }

    @Test
    public void CreatesRepository() {
        RulesDefinition.Context context = mock(RulesDefinition.Context.class, RETURNS_DEEP_STUBS);
        definition.define(context);

        verify(context).createRepository(eq(EsRulesDefinition.REPOSITORY_NAME), eq(EsLintLanguage.LANGUAGE_KEY));
    }

    @Test
    public void ConfiguresAdditionalRules() {
        // cfg1
        RulesDefinition.Rule rule1 = this.getRule("custom-rule-1");
        assertNull(rule1);

        // cfg2
        RulesDefinition.Rule rule2 = this.getRule("custom-rule-2");
        assertNotNull(rule2);
        assertEquals("test rule #2", rule2.name());
        assertEquals(Severity.MINOR, rule2.severity());
        assertEquals("#2 description", rule2.htmlDescription());
        assertEquals(null, rule2.debtRemediationFunction());
        assertEquals(RuleType.CODE_SMELL, rule2.type());

        // cfg3
        RulesDefinition.Rule rule3 = this.getRule("custom-rule-3");
        assertNotNull(rule3);
        assertEquals("test rule #3", rule3.name());
        assertEquals(Severity.INFO, rule3.severity());
        assertEquals("#3 description", rule3.htmlDescription());
        assertEquals(
                Type.CONSTANT_ISSUE,
            rule3.debtRemediationFunction().type()
        );
        assertEquals(null, rule3.debtRemediationFunction().gapMultiplier());
        assertEquals("15min", rule3.debtRemediationFunction().baseEffort());
        assertEquals(RuleType.CODE_SMELL, rule3.type());

        // cfg4
        RulesDefinition.Rule rule4 = this.getRule("custom-rule-4");
        assertNotNull(rule4);
        assertEquals("test rule #4", rule4.name());
        assertEquals(Severity.MINOR, rule4.severity());
        assertEquals("#4 description", rule4.htmlDescription());
        assertEquals(
                Type.LINEAR,
            rule4.debtRemediationFunction().type()
        );
        assertEquals("5min", rule4.debtRemediationFunction().gapMultiplier());
        assertEquals(null, rule4.debtRemediationFunction().baseEffort());
        assertEquals(RuleType.BUG, rule4.type());

        // cfg5
        RulesDefinition.Rule rule5 = this.getRule("custom-rule-5");
        assertNotNull(rule5);
        assertEquals("test rule #5", rule5.name());
        assertEquals(Severity.MAJOR, rule5.severity());
        assertEquals("#5 description", rule5.htmlDescription());
        assertEquals(RuleType.VULNERABILITY, rule5.type());

        assertEquals("30min", rule5.debtRemediationFunction().gapMultiplier());
        assertEquals("15min", rule5.debtRemediationFunction().baseEffort());
    }

    @Test
    public void LoadRulesFromInvalidStream() throws IOException {
        List<EsLintRule> rules = new ArrayList<>();
        InputStream testStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test exception");
            }
        };
        EsRulesDefinition.loadRules(testStream, rules);
    }


    @Test
    public void CheckAdditionalRulesConfigProvided() {
        EsRulesDefinition rulesDef = new EsRulesDefinition(settings);
        List<EsLintRule> rules = rulesDef.getRules();
        assertNotNull(rules);
        assertEquals(4, rules.size()); // 4 enabled rules, 1 disabled rule
    }

    @Test
    public void CheckCustomRulesConfigNotProvided() {

        Settings settings = mock(Settings.class);
        when(settings.getKeysStartingWith(EsLintPlugin.SETTING_ES_RULE_CONFIGS)).thenReturn(new ArrayList<String>());

        EsRulesDefinition rulesDef = new EsRulesDefinition(settings);
        List<EsLintRule> rules = rulesDef.getRules();
        assertNotNull(rules);
        assertEquals(0, rules.size());
    }

    private RulesDefinition.Rule getRule(String name) {
        return context.repository(EsRulesDefinition.REPOSITORY_NAME).rule(name);
    }
}
