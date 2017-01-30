package io.github.sleroy.sonar;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;

public class EsLintRuleProfileTest {
    ValidationMessages validationMessages;
    EsLintRuleProfile ruleProfile;

    HashSet<String> expectedRuleNames;

    @Before
    public void setUp() throws Exception {
        this.validationMessages = ValidationMessages.create();
        this.ruleProfile = new EsLintRuleProfile();
        this.expectedRuleNames = new HashSet<String>(Arrays.asList(
                EsRulesDefinition.ESLINT_UNKNOWN_RULE.key,
                "align",
                "adjacent-overload-signatures",
                "array-type",
                "arrow-parens",
                "ban",
                "class-name",
                "comment-format",
                "completed-docs",
                "curly",
                "cyclomatic-complexity",
                "eofline",
                "file-header",
                "forin",
                "indent",
                "interface-name",
                "jsdoc-format",
                "label-position",
                "label-undefined",
                "linebreak-style",
                "max-classes-per-file",
                "max-file-line-count",
                "max-line-length",
                "member-access",
                "member-ordering",
                "new-parens",
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
                "no-default-export",
                "no-duplicate-key",
                "no-duplicate-variable",
                "no-empty",
                "no-eval",
                "no-for-in-array",
                "no-inferrable-types",
                "no-internal-module",
                "no-invalid-this",
                "no-mergeable-namespace",
                "no-namespace",
                "no-null-keyword",
                "no-parameter-properties",
                "no-reference",
                "no-require-imports",
                "no-shadowed-variable",
                "no-string-literal",
                "no-switch-case-fall-through",
                "no-trailing-whitespace",
                "no-unreachable",
                "no-unsafe-finally",
                "no-unused-expression",
                "no-unused-new",
                "no-unused-variable",
                "no-use-before-declare",
                "no-var-keyword",
                "no-var-requires",
                "prefer-for-of",
                "object-literal-key-quotes",
                "object-literal-shorthand",
                "object-literal-sort-keys",
                "one-line",
                "one-variable-per-declaration",
                "only-arrow-functions",
                "ordered-imports",
                "quotemark",
                "radix",
                "restrict-plus-operands",
                "semicolon",
                "switch-default",
                "trailing-comma",
                "triple-equals",
                "typedef",
                "typedef-whitespace",
                "use-strict",
                "use-isnan",
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
            assertNotNull("Expected rule missing in plugin: " + ruleName, profile.getActiveRule(EsRulesDefinition.REPOSITORY_NAME, ruleName));
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
