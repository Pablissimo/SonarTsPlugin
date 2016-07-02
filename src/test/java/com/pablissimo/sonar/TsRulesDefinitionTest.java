package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class TsRulesDefinitionTest {

    Settings settings;
    TsRulesDefinition definition;
    Context context;

    @Before
    public void setUp() throws Exception {

        this.settings = mock(Settings.class);
        when(this.settings.getString(TypeScriptPlugin.SETTING_TS_LINT_CUSTOM_RULES_CONFIG))
            .thenReturn(
                "custom-rule-1=false\n" +
                "custom-rule-1.name=test rule #1\n" +
                "custom-rule-1.severity=MAJOR\n" +
                "custom-rule-1.description=#1 description\n" +
                "\n" +
                "custom-rule-2=true\n" +
                "custom-rule-2.name=test rule #2\n" +
                "custom-rule-2.severity=MINOR\n" +
                "custom-rule-2.description=#2 description\n" +
                "\n");

        this.definition = new TsRulesDefinition(this.settings);
        this.context = new Context();
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
    public void ConfiguresCustomRule() {
        Rule rule = getRule("custom-rule-2");
        assertNotNull(rule);
        assertEquals(Severity.MINOR, rule.severity());
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

    private Rule getRule(String name) {
        this.definition.define(context);
        return this.context.repository(TsRulesDefinition.REPOSITORY_NAME).rule(name);
    }

    private RulesDefinition.Param getParam(Rule rule, String name) {
        return rule.param(name);
    }
}
