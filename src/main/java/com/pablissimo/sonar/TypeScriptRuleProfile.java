package com.pablissimo.sonar;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfile extends ProfileDefinition {
    public static final String PROFILE_NAME = "tslint";

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        RulesProfile profile = RulesProfile.create("TsLint", TypeScriptLanguage.LANGUAGE_KEY);

        activateRule(profile, TsRulesDefinition.RULE_TSLINT_ISSUE);
        activateRule(profile, TsRulesDefinition.RULE_ALIGN);
        activateRule(profile, TsRulesDefinition.RULE_BAN);
        activateRule(profile, TsRulesDefinition.RULE_CLASS_NAME);
        activateRule(profile, TsRulesDefinition.RULE_COMMENT_FORMAT);
        activateRule(profile, TsRulesDefinition.RULE_CURLY);
        activateRule(profile, TsRulesDefinition.RULE_EOFLINE);
        activateRule(profile, TsRulesDefinition.RULE_FORIN);
        activateRule(profile, TsRulesDefinition.RULE_INDENT);
        activateRule(profile, TsRulesDefinition.RULE_INTERFACE_NAME);
        activateRule(profile, TsRulesDefinition.RULE_JSDOC_FORMAT);
        activateRule(profile, TsRulesDefinition.RULE_LABEL_POSITION);
        activateRule(profile, TsRulesDefinition.RULE_LABEL_UNDEFINED);
        activateRule(profile, TsRulesDefinition.RULE_MAX_LINE_LENGTH);
        activateRule(profile, TsRulesDefinition.RULE_MEMBER_ACCESS);
        activateRule(profile, TsRulesDefinition.RULE_MEMBER_ORDERING);
        activateRule(profile, TsRulesDefinition.RULE_NO_ANY);
        activateRule(profile, TsRulesDefinition.RULE_NO_ARG);
        activateRule(profile, TsRulesDefinition.RULE_NO_BITWISE);
        activateRule(profile, TsRulesDefinition.RULE_NO_CONDITIONAL_ASSIGNMENT);
        activateRule(profile, TsRulesDefinition.RULE_NO_CONSECUTIVE_BLANK_LINES);
        activateRule(profile, TsRulesDefinition.RULE_NO_CONSOLE);
        activateRule(profile, TsRulesDefinition.RULE_NO_CONSTRUCT);
        activateRule(profile, TsRulesDefinition.RULE_NO_CONSTRUCTOR_VARS);
        activateRule(profile, TsRulesDefinition.RULE_NO_DEBUGGER);
        activateRule(profile, TsRulesDefinition.RULE_NO_DUPLICATE_KEY);
        activateRule(profile, TsRulesDefinition.RULE_NO_DUPLICATE_VARIABLE);
        activateRule(profile, TsRulesDefinition.RULE_NO_EMPTY);
        activateRule(profile, TsRulesDefinition.RULE_NO_EVAL);
        activateRule(profile, TsRulesDefinition.RULE_NO_INFERRABLE_TYPES);
        activateRule(profile, TsRulesDefinition.RULE_NO_INTERNAL_MODULE);
        activateRule(profile, TsRulesDefinition.RULE_NO_NULL_KEYWORD);
        activateRule(profile, TsRulesDefinition.RULE_NO_REQUIRE_IMPORTS);
        activateRule(profile, TsRulesDefinition.RULE_NO_SHADOWED_VARIABLE);
        activateRule(profile, TsRulesDefinition.RULE_NO_STRING_LITERAL);
        activateRule(profile, TsRulesDefinition.RULE_NO_SWITCH_CASE_FALL_THROUGH);
        activateRule(profile, TsRulesDefinition.RULE_NO_TRAILING_WHITESPACE);
        activateRule(profile, TsRulesDefinition.RULE_NO_UNREACHABLE);
        activateRule(profile, TsRulesDefinition.RULE_NO_UNUSED_EXPRESSION);
        activateRule(profile, TsRulesDefinition.RULE_NO_UNUSED_VARIABLE);
        activateRule(profile, TsRulesDefinition.RULE_NO_USE_BEFORE_DECLARE);
        activateRule(profile, TsRulesDefinition.RULE_NO_VAR_KEYWORD);
        activateRule(profile, TsRulesDefinition.RULE_NO_VAR_REQUIRES);
        activateRule(profile, TsRulesDefinition.RULE_OBJECT_LITERAL_SORT_KEYS);
        activateRule(profile, TsRulesDefinition.RULE_ONE_LINE);
        activateRule(profile, TsRulesDefinition.RULE_QUOTEMARK);
        activateRule(profile, TsRulesDefinition.RULE_RADIX);
        activateRule(profile, TsRulesDefinition.RULE_SEMICOLON);
        activateRule(profile, TsRulesDefinition.RULE_SWITCH_DEFAULT);
        activateRule(profile, TsRulesDefinition.RULE_TRAILING_COMMA);
        activateRule(profile, TsRulesDefinition.RULE_TRIPLE_EQUALS);
        activateRule(profile, TsRulesDefinition.RULE_TYPEDEF);
        activateRule(profile, TsRulesDefinition.RULE_TYPEDEF_WHITESPACE);
        activateRule(profile, TsRulesDefinition.RULE_USE_STRICT);
        activateRule(profile, TsRulesDefinition.RULE_VARIABLE_NAME);
        activateRule(profile, TsRulesDefinition.RULE_WHITESPACE);

        return profile;
    }

    private static void activateRule(RulesProfile profile, String ruleKey) {
        profile.activateRule(Rule.create(TsRulesDefinition.REPOSITORY_NAME, ruleKey), null);
    }
}
