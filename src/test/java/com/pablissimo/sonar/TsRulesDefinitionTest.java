package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class TsRulesDefinitionTest {

    Settings settings;
    TsRulesDefinition definition;
    Context context;

    @Before
    public void setUp() throws Exception {

        this.settings = mock(Settings.class);

        when(this.settings.getKeysStartingWith(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS))
            .thenReturn(new ArrayList<String>() {{
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg1.name");
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg1.config");
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg2.name");
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg2.config");
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg3.name");
                add(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg3.config");
            }});

        // config with one disabled rule
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg1.config"))
            .thenReturn(
                "custom-rule-1=false\n" +
                "custom-rule-1.name=test rule #1\n" +
                "custom-rule-1.severity=MAJOR\n" +
                "custom-rule-1.description=#1 description\n" +
                "\n"
            );

        // config with a basic rule (no debt settings)
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg2.config"))
            .thenReturn(
                "custom-rule-2=true\n" +
                "custom-rule-2.name=test rule #2\n" +
                "custom-rule-2.severity=MINOR\n" +
                "custom-rule-2.description=#2 description\n" +
                "\n"
            );

        // config with a advanced rules (including debt settings)
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS + ".cfg3.config"))
            .thenReturn(
                "custom-rule-3=true\n" +
                "custom-rule-3.name=test rule #3\n" +
                "custom-rule-3.severity=INFO\n" +
                "custom-rule-3.description=#3 description\n" +
                "custom-rule-3.debtFunc=" + DebtRemediationFunction.Type.CONSTANT_ISSUE + "\n" +
                "custom-rule-3.debtScalar=15min\n" +
                "custom-rule-3.debtOffset=1min\n" +
                "custom-rule-3.debtType=INVALID_TYPE_GOES_HERE\n" +
                "\n" +
                "custom-rule-4=true\n" +
                "custom-rule-4.name=test rule #4\n" +
                "custom-rule-4.severity=MINOR\n" +
                "custom-rule-4.description=#4 description\n" +
                "custom-rule-4.debtFunc=" + DebtRemediationFunction.Type.LINEAR + "\n" +
                "custom-rule-4.debtScalar=5min\n" +
                "custom-rule-4.debtOffset=2h\n" +
                "custom-rule-4.debtType=" + RuleType.BUG.name() + "\n" +
                "\n" +
                "custom-rule-5=true\n" +
                "custom-rule-5.name=test rule #5\n" +
                "custom-rule-5.severity=MAJOR\n" +
                "custom-rule-5.description=#5 description\n" +
                "custom-rule-5.debtFunc=" + DebtRemediationFunction.Type.LINEAR_OFFSET + "\n" +
                "custom-rule-5.debtScalar=30min\n" +
                "custom-rule-5.debtOffset=15min\n" +
                "custom-rule-5.debtType=" + RuleType.VULNERABILITY.name() + "\n" +
                "\n"
            );

        this.definition = new TsRulesDefinition(this.settings);
        this.context = new Context();
        this.definition.define(context);
    }

    @Test
    public void CreatesRepository() {
        Context context = mock(Context.class, RETURNS_DEEP_STUBS);
        this.definition.define(context);

        verify(context).createRepository(eq(TsRulesDefinition.REPOSITORY_NAME), eq(TypeScriptLanguage.LANGUAGE_KEY));
    }

    @Test
    public void ConfiguresAlignRule() {
        Rule rule = getRule("align");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresBanRule() {
        Rule rule = getRule("ban");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresClassNameRule() {
        Rule rule = getRule("class-name");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresCommentFormatRule() {
        Rule rule = getRule("comment-format");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresCurlyRule() {
        Rule rule = getRule("curly");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresEofLineRule() {
        Rule rule = getRule("eofline");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresForInRule() {
        Rule rule = getRule("forin");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresIndentRule() {
        Rule rule = getRule("indent");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresInterfaceNameRule() {
        Rule rule = getRule("interface-name");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresJsDocFormat() {
        Rule rule = getRule("jsdoc-format");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresLabelPositionRule() {
        Rule rule = getRule("label-position");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresLabelUndefinedRule() {
        Rule rule = getRule("label-undefined");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresMaxLineLengthRule() {
        Rule rule = getRule("max-line-length");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresMemberAccessRule() {
        Rule rule = getRule("member-access");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresMemberOrderingRule() {
        Rule rule = getRule("member-ordering");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoAngleBracketTypeAssertionRule() {
        Rule rule = getRule("no-angle-bracket-type-assertion");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoAnyRule() {
        Rule rule = getRule("no-any");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoArgRule() {
        Rule rule = getRule("no-arg");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoBitwiseRule() {
        Rule rule = getRule("no-bitwise");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoConditionalAssignmentRule() {
        Rule rule = getRule("no-conditional-assignment");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoConsecutiveBlankLinesRule() {
        Rule rule = getRule("no-consecutive-blank-lines");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresNoConsoleRule() {
        Rule rule = getRule("no-console");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoConstructRule() {
        Rule rule = getRule("no-construct");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoConstructorVarsRule() {
        Rule rule = getRule("no-constructor-vars");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoDebuggerRule() {
        Rule rule = getRule("no-debugger");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresNoDuplicateKeyRule() {
        Rule rule = getRule("no-duplicate-key");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoDuplicateVariableRule() {
        Rule rule = getRule("no-duplicate-variable");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoEmptyRule() {
        Rule rule = getRule("no-empty");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoEvalRule() {
        Rule rule = getRule("no-eval");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresNoInferrableTypesRule() {
        Rule rule = getRule("no-inferrable-types");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoInternalModuleRule() {
        Rule rule = getRule("no-internal-module");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoNullKeywordRule() {
        Rule rule = getRule("no-null-keyword");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoRequireImportsRule() {
        Rule rule = getRule("no-require-imports");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoShadowedVariableRule() {
        Rule rule = getRule("no-shadowed-variable");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoStringLiteralRule() {
        Rule rule = getRule("no-string-literal");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoSwitchCaseFallThroughRule() {
        Rule rule = getRule("no-switch-case-fall-through");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoTrailingWhitespaceRule() {
        Rule rule = getRule("no-trailing-whitespace");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresNoUnreachableRule() {
        Rule rule = getRule("no-unreachable");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoUnusedExpressionRule() {
        Rule rule = getRule("no-unused-expression");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoUnusedVariableRule() {
        Rule rule = getRule("no-unused-variable");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoUseBeforeDeclareRule() {
        Rule rule = getRule("no-use-before-declare");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresNoVarKeywordRule() {
        Rule rule = getRule("no-var-keyword");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresNoVarRequiresRule() {
        Rule rule = getRule("no-var-requires");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresObjectLiteralSortKeysRule() {
        Rule rule = getRule("object-literal-sort-keys");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresOneLineRule() {
        Rule rule = getRule("one-line");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresQuoteMarkRule() {
        Rule rule = getRule("quotemark");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresRadixRule() {
        Rule rule = getRule("radix");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresSemicolonRule() {
        Rule rule = getRule("semicolon");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresSwitchDefaultRule() {
        Rule rule = getRule("switch-default");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresTrailingCommaRule() {
        Rule rule = getRule("trailing-comma");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresTripleEqualsCommaRule() {
        Rule rule = getRule("triple-equals");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresTypedefRule() {
        Rule rule = getRule("typedef");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresTypedefWhitespaceRule() {
        Rule rule = getRule("typedef-whitespace");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresUseStrictRule() {
        Rule rule = getRule("use-strict");
        assertNotNull(rule);
        assertEquals(Severity.CRITICAL, rule.severity());
    }

    @Test
    public void ConfiguresVariableNameRule() {
        Rule rule = getRule("variable-name");
        assertNotNull(rule);
        assertEquals(Severity.MAJOR, rule.severity());
    }

    @Test
    public void ConfiguresWhitespaceRule() {
        Rule rule = getRule("whitespace");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
    }

    @Test
    public void ConfiguresAdditionalRules() {
        // cfg1
        Rule rule1 = getRule("custom-rule-1");
        assertNull(rule1);

        // cfg2
        Rule rule2 = getRule("custom-rule-2");
        assertNotNull(rule2);
        assertEquals("test rule #2", rule2.name());
        assertEquals(Severity.MINOR, rule2.severity());
        assertEquals("#2 description", rule2.htmlDescription());
        assertEquals(null, rule2.debtRemediationFunction());
        assertEquals(RuleType.CODE_SMELL, rule2.type());

        // cfg3
        Rule rule3 = getRule("custom-rule-3");
        assertNotNull(rule3);
        assertEquals("test rule #3", rule3.name());
        assertEquals(Severity.INFO, rule3.severity());
        assertEquals("#3 description", rule3.htmlDescription());
        assertEquals(
            DebtRemediationFunction.Type.CONSTANT_ISSUE,
            rule3.debtRemediationFunction().type()
        );
        assertEquals(null, rule3.debtRemediationFunction().gapMultiplier());
        assertEquals("15min", rule3.debtRemediationFunction().baseEffort());
        assertEquals(RuleType.CODE_SMELL, rule3.type());

        // cfg4
        Rule rule4 = getRule("custom-rule-4");
        assertNotNull(rule4);
        assertEquals("test rule #4", rule4.name());
        assertEquals(Severity.MINOR, rule4.severity());
        assertEquals("#4 description", rule4.htmlDescription());
        assertEquals(
            DebtRemediationFunction.Type.LINEAR,
            rule4.debtRemediationFunction().type()
        );
        assertEquals("5min", rule4.debtRemediationFunction().gapMultiplier());
        assertEquals(null, rule4.debtRemediationFunction().baseEffort());
        assertEquals(RuleType.BUG, rule4.type());

        // cfg5
        Rule rule5 = getRule("custom-rule-5");
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
        List<TsLintRule> rules = new ArrayList<>();
        InputStream testStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Test exception");
            }
        };
        TsRulesDefinition.loadRules(testStream, rules);
    }

    @Test
    public void CheckAdditionalRulesConfigProvided() {
        TsRulesDefinition rulesDef = new TsRulesDefinition(this.settings);
        List<TsLintRule> rules = rulesDef.getRules();
        assertNotNull(rules);
        assertEquals(4, rules.size()); // 4 enabled rules, 1 disabled rule
    }

    @Test
    public void CheckCustomRulesConfigNotProvided() {

        Settings settings = mock(Settings.class);
        when(settings.getKeysStartingWith(TypeScriptPlugin.SETTING_TS_RULE_CONFIGS)).thenReturn(new ArrayList<String>());

        TsRulesDefinition rulesDef = new TsRulesDefinition(settings);
        List<TsLintRule> rules = rulesDef.getRules();
        assertNotNull(rules);
        assertEquals(0, rules.size());
    }

    private Rule getRule(String name) {
        return this.context.repository(TsRulesDefinition.REPOSITORY_NAME).rule(name);
    }
}
