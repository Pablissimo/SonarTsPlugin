package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfileTest {
    ValidationMessages validationMessages;
    TypeScriptRuleProfile ruleProfile;

    HashSet<String> expectedRuleNames;

    @Before
    public void setUp() throws Exception {
        this.validationMessages = ValidationMessages.create();
        this.ruleProfile = new TypeScriptRuleProfile();
        this.expectedRuleNames = new HashSet<String>(Arrays.asList(
                TsRulesDefinition.TSLINT_UNKNOWN_RULE.key,
                "align",
                "ban",
                "class-name",
                "comment-format",
                "curly",
                "eofline",
                "forin",
                "indent",
                "interface-name",
                "jsdoc-format",
                "label-position",
                "label-undefined",
                "max-line-length",
                "member-access",
                "member-ordering",
                "no-angle-bracket-type-assertion",
                "no-any",
                "no-arg",
                "no-bitwise",
                "no-conditional-assignment",
                "no-consecutive-blank-lines",
                "no-console",
                "no-construct",
                "no-constructor-vars",
                "no-debugger",
                "no-duplicate-key",
                "no-duplicate-variable",
                "no-empty",
                "no-eval",
                "no-inferrable-types",
                "no-internal-module",
                "no-null-keyword",
                "no-require-imports",
                "no-shadowed-variable",
                "no-string-literal",
                "no-switch-case-fall-through",
                "no-trailing-whitespace",
                "no-unreachable",
                "no-unused-expression",
                "no-unused-variable",
                "no-use-before-declare",
                "no-var-keyword",
                "no-var-requires",
                "object-literal-sort-keys",
                "one-line",
                "quotemark",
                "radix",
                "semicolon",
                "switch-default",
                "trailing-comma",
                "triple-equals",
                "typedef",
                "typedef-whitespace",
                "use-strict",
                "variable-name",
                "whitespace"
                ));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void definesExpectedRules() {
        RulesProfile profile = this.ruleProfile.createProfile(this.validationMessages);

        for (String ruleName : this.expectedRuleNames) {
            assertNotNull("Expected rule missing in plugin: " + ruleName, profile.getActiveRule(TsRulesDefinition.REPOSITORY_NAME, ruleName));
        }
    }

    @Test
    public void definesUnexpectedRules() {
        RulesProfile profile = this.ruleProfile.createProfile(this.validationMessages);

        for (ActiveRule rule : profile.getActiveRules()) {
            assertTrue("Unexpected rule in plugin: " + rule.getRuleKey(), this.expectedRuleNames.contains(rule.getRuleKey()));
        }
    }
}
