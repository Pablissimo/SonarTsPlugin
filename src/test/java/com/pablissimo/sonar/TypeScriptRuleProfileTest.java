package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfileTest {
    ValidationMessages validationMessages;
    TypeScriptRuleProfile ruleProfile;

    List<String> expectedRuleNames;

    @Before
    public void setUp() throws Exception {
        this.validationMessages = ValidationMessages.create();
        this.ruleProfile = new TypeScriptRuleProfile();
        this.expectedRuleNames = new ArrayList<String>(Arrays.asList(
                TsRulesDefinition.RULE_TSLINT_ISSUE,
                TsRulesDefinition.RULE_ALIGN,
                TsRulesDefinition.RULE_BAN,
                TsRulesDefinition.RULE_CLASS_NAME,
                TsRulesDefinition.RULE_COMMENT_FORMAT,
                TsRulesDefinition.RULE_CURLY,
                TsRulesDefinition.RULE_EOFLINE,
                TsRulesDefinition.RULE_FORIN,
                TsRulesDefinition.RULE_INDENT,
                TsRulesDefinition.RULE_INTERFACE_NAME,
                TsRulesDefinition.RULE_JSDOC_FORMAT,
                TsRulesDefinition.RULE_LABEL_POSITION,
                TsRulesDefinition.RULE_LABEL_UNDEFINED,
                TsRulesDefinition.RULE_MAX_LINE_LENGTH,
                TsRulesDefinition.RULE_MEMBER_ACCESS,
                TsRulesDefinition.RULE_MEMBER_ORDERING,
                TsRulesDefinition.RULE_NO_ANY,
                TsRulesDefinition.RULE_NO_ARG,
                TsRulesDefinition.RULE_NO_BITWISE,
                TsRulesDefinition.RULE_NO_CONDITIONAL_ASSIGNMENT,
                TsRulesDefinition.RULE_NO_CONSECUTIVE_BLANK_LINES,
                TsRulesDefinition.RULE_NO_CONSOLE,
                TsRulesDefinition.RULE_NO_CONSTRUCT,
                TsRulesDefinition.RULE_NO_CONSTRUCTOR_VARS,
                TsRulesDefinition.RULE_NO_DEBUGGER,
                TsRulesDefinition.RULE_NO_DUPLICATE_KEY,
                TsRulesDefinition.RULE_NO_DUPLICATE_VARIABLE,
                TsRulesDefinition.RULE_NO_EMPTY,
                TsRulesDefinition.RULE_NO_EVAL,
                TsRulesDefinition.RULE_NO_INFERRABLE_TYPES,
                TsRulesDefinition.RULE_NO_INTERNAL_MODULE,
                TsRulesDefinition.RULE_NO_NULL_KEYWORD,
                TsRulesDefinition.RULE_NO_REQUIRE_IMPORTS,
                TsRulesDefinition.RULE_NO_SHADOWED_VARIABLE,
                TsRulesDefinition.RULE_NO_STRING_LITERAL,
                TsRulesDefinition.RULE_NO_SWITCH_CASE_FALL_THROUGH,
                TsRulesDefinition.RULE_NO_TRAILING_WHITESPACE,
                TsRulesDefinition.RULE_NO_UNREACHABLE,
                TsRulesDefinition.RULE_NO_UNUSED_EXPRESSION,
                TsRulesDefinition.RULE_NO_UNUSED_VARIABLE,
                TsRulesDefinition.RULE_NO_USE_BEFORE_DECLARE,
                TsRulesDefinition.RULE_NO_VAR_KEYWORD,
                TsRulesDefinition.RULE_NO_VAR_REQUIRES,
                TsRulesDefinition.RULE_OBJECT_LITERAL_SORT_KEYS,
                TsRulesDefinition.RULE_ONE_LINE,
                TsRulesDefinition.RULE_QUOTEMARK,
                TsRulesDefinition.RULE_RADIX,
                TsRulesDefinition.RULE_SEMICOLON,
                TsRulesDefinition.RULE_SWITCH_DEFAULT,
                TsRulesDefinition.RULE_TRAILING_COMMA,
                TsRulesDefinition.RULE_TRIPLE_EQUALS,
                TsRulesDefinition.RULE_TYPEDEF,
                TsRulesDefinition.RULE_TYPEDEF_WHITESPACE,
                TsRulesDefinition.RULE_USE_STRICT,
                TsRulesDefinition.RULE_VARIABLE_NAME,
                TsRulesDefinition.RULE_WHITESPACE
                ));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void definesExpectedRules() {
        RulesProfile profile = this.ruleProfile.createProfile(this.validationMessages);

        for (String ruleName : this.expectedRuleNames) {
            assertNotNull(profile.getActiveRule(TsRulesDefinition.REPOSITORY_NAME, ruleName));
        }
    }
}
