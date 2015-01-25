package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.sonar.api.batch.rule.RuleParam;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Context;
import org.sonar.api.server.rule.RulesDefinition.NewRepository;
import org.sonar.api.server.rule.RulesDefinition.NewRule;
import org.sonar.api.server.rule.RulesDefinition.Rule;

public class TsRulesDefinitionTest {
	TsRulesDefinition definition;
	Context context;	
	
	@Before
	public void setUp() throws Exception {
		this.definition = new TsRulesDefinition();
		this.context = new Context();
	}
	
	@Test
	public void CreatesRepository() {
		Context context = mock(Context.class, RETURNS_DEEP_STUBS);
		this.definition.define(context);
		
		verify(context).createRepository(eq(TsRulesDefinition.REPOSITORY_NAME), eq(TypeScriptLanguage.LANGUAGE_EXTENSION));
	}
	
	@Test
	public void ConfiguresBanRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_BAN);
		assertEquals(Severity.CRITICAL, rule.severity());
	}
	
	@Test
	public void ConfiguresClassNameRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_CLASS_NAME);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresCommentFormatRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_COMMENT_FORMAT);
		assertEquals(Severity.MINOR, rule.severity());
		
		assertEquals(2, rule.params().size());
		
		RulesDefinition.Param checkSpaceParam = getParam(rule, TsRulesDefinition.RULE_PARAM_CHECK_SPACE);
		assertEquals(RuleParamType.BOOLEAN, checkSpaceParam.type());
		assertEquals("true", checkSpaceParam.defaultValue());
		
		RulesDefinition.Param checkLowerCaseParam = getParam(rule, TsRulesDefinition.RULE_PARAM_CHECK_LOWERCASE);
		assertEquals(RuleParamType.BOOLEAN, checkLowerCaseParam.type());
		assertEquals("false", checkLowerCaseParam.defaultValue());
	}
	
	@Test
	public void ConfiguresCurlyRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_CURLY);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresEofLineRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_EOFLINE);
		assertEquals(Severity.MINOR, rule.severity());
	}
	
	@Test
	public void ConfiguresForInRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_FORIN);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresIndentRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_INDENT);
		assertEquals(Severity.MINOR, rule.severity());
		
		RulesDefinition.Param tabsOrSpacesParam = getParam(rule, TsRulesDefinition.RULE_PARAM_TABS_OR_SPACES);
		assertEquals("tabs", tabsOrSpacesParam.defaultValue());
		assertEquals(2, tabsOrSpacesParam.type().values().size());
		assertTrue(tabsOrSpacesParam.type().values().contains("tabs"));
		assertTrue(tabsOrSpacesParam.type().values().contains("spaces"));
	}
	
	@Test
	public void ConfiguresInterfaceNameRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_INTERFACE_NAME);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresJsDocFormat() {
		Rule rule = getRule(TsRulesDefinition.RULE_JSDOC_FORMAT);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresLabelPositionRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_LABEL_POSITION);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresLabelUndefinedRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_LABEL_UNDEFINED);
		assertEquals(Severity.CRITICAL, rule.severity());
	}
	
	@Test
	public void ConfiguresMaxLineLengthRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_MAX_LINE_LENGTH);
		assertEquals(Severity.MAJOR, rule.severity());
		
		RulesDefinition.Param maxLineLengthParam = getParam(rule, TsRulesDefinition.RULE_PARAM_MAX_LINE_LENGTH);
		assertEquals("120", maxLineLengthParam.defaultValue());
		assertEquals(RuleParamType.INTEGER, maxLineLengthParam.type());
	}
	
	@Test
	public void ConfiguresMemberOrderingRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_MEMBER_ORDERING);
		assertEquals(Severity.MAJOR, rule.severity());
		
		RulesDefinition.Param publicBeforePrivateParam = getParam(rule, TsRulesDefinition.RULE_PARAM_PUBLIC_BEFORE_PRIVATE);
		assertEquals(RuleParamType.BOOLEAN, publicBeforePrivateParam.type());
		assertEquals("true", publicBeforePrivateParam.defaultValue());
		
		RulesDefinition.Param staticBeforeInstanceParam = getParam(rule, TsRulesDefinition.RULE_PARAM_STATIC_BEFORE_INSTANCE);
		assertEquals(RuleParamType.BOOLEAN, staticBeforeInstanceParam.type());
		assertEquals("true", staticBeforeInstanceParam.defaultValue());
		
		RulesDefinition.Param variablesBeforeFunctionsParam = getParam(rule, TsRulesDefinition.RULE_PARAM_VARIABLES_BEFORE_FUNCTIONS);
		assertEquals(RuleParamType.BOOLEAN, variablesBeforeFunctionsParam.type());
		assertEquals("true", variablesBeforeFunctionsParam.defaultValue());
	}
	
	@Test
	public void ConfiguresNoAnyRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_ANY);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoArgRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_ARG);
		assertEquals(Severity.MAJOR, rule.severity());
	}

	@Test
	public void ConfiguresNoBitwiseRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_BITWISE);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoConsoleRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_CONSOLE);
		assertEquals(Severity.MAJOR, rule.severity());
		
		RulesDefinition.Param bannedFunctionsParam = getParam(rule, TsRulesDefinition.RULE_PARAM_BANNED_CONSOLE_FUNCTIONS);
		assertEquals(RuleParamType.STRING, bannedFunctionsParam.type());
		assertNull(bannedFunctionsParam.defaultValue());
	}

	@Test
	public void ConfiguresNoConsecutiveBlankLinesRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_CONSECUTIVE_BLANK_LINES);
		assertEquals(Severity.MINOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoConstructRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_CONSTRUCT);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoConstructorVarsRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_CONSTRUCTOR_VARS);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoDebuggerRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_DEBUGGER);
		assertEquals(Severity.CRITICAL, rule.severity());
	}
	
	@Test
	public void ConfiguresNoDuplicateKeyRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_DUPLICATE_KEY);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoDuplicateVariableRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_DUPLICATE_VARIABLE);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoEmptyRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_EMPTY);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoEvalRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_EVAL);
		assertEquals(Severity.CRITICAL, rule.severity());
	}
	
	@Test
	public void ConfiguresNoStringLiteralRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_STRING_LITERAL);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoSwitchFallThroughRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_SWITCH_CASE_FALL_THROUGH);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoTrailingCommaRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_TRAILING_COMMA);
		assertEquals(Severity.MINOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoTrailingWhitespaceRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_TRAILING_WHITESPACE);
		assertEquals(Severity.MINOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoUnreachableRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_UNREACHABLE);
		assertEquals(Severity.MAJOR, rule.severity());
	}
	
	@Test
	public void ConfiguresNoUnusedExpressionRule() {
		Rule rule = getRule(TsRulesDefinition.RULE_NO_UNUSED_EXPRESSION);
		assertEquals(Severity.MAJOR, rule.severity());
	}
		
	private Rule getRule(String name) {
		this.definition.define(context);
		return this.context.repository(TsRulesDefinition.REPOSITORY_NAME).rule(name);
	}
	
	private RulesDefinition.Param getParam(Rule rule, String name) {
		return rule.param(name);
	}
}
