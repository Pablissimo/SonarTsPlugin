package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;

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
                "adjacent-overload-signatures",
                "array-type",
                "arrow-return-shorthand",
                "arrow-parens",
                "await-promise",
                "ban",
                "ban-types",
                "callable-types",
                "class-name",
                "comment-format",
                "completed-docs",
                "curly",
                "cyclomatic-complexity",
                "eofline",
                "file-header",
                "forin",
                "import-blacklist",
                "import-spacing",
                "indent",
                "interface-name",
                "interface-over-type-literal",
                "jsdoc-format",
                "label-position",
                "label-undefined",
                "linebreak-style",
                "match-default-export-name",
                "max-classes-per-file",
                "max-file-line-count",
                "max-line-length",
                "member-access",
                "member-ordering",
                "newline-before-return",
                "new-parens",
                "no-angle-bracket-type-assertion",
                "no-any",
                "no-arg",
                "no-bitwise",
                "no-boolean-literal-compare",
                "no-conditional-assignment",
                "no-consecutive-blank-lines",
                "no-console",
                "no-construct",
                "no-constructor-vars",
                "no-debugger",
                "no-default-export",
                "no-duplicate-key",
                "no-duplicate-super",
                "no-duplicate-variable",
                "no-empty",
                "no-empty-interface",
                "no-eval",
                "no-for-in-array",
                "no-floating-promises",
                "no-import-side-effect",
                "no-non-null-assertion",
                "no-inferrable-types",
                "no-inferred-empty-object-type",
                "no-internal-module",
                "no-invalid-this",
                "no-magic-numbers",
                "no-misused-new",
                "no-mergeable-namespace",
                "no-namespace",
                "no-null-keyword",
                "no-parameter-properties",
                "no-reference",
                "no-require-imports",
                "no-shadowed-variable",
                "no-string-literal",
                "no-string-throw",
                "no-switch-case-fall-through",
                "no-trailing-whitespace",
                "no-unnecessary-initializer",
                "no-unnecessary-qualifier",
                "no-unbound-method",
                "no-unreachable",
                "no-unsafe-any",
                "no-unsafe-finally",
                "no-unused-expression",
                "no-unused-new",
                "no-unused-variable",
                "no-use-before-declare",
                "no-var-keyword",
                "no-var-requires",
                "no-void-expression",
                "object-literal-key-quotes",
                "object-literal-shorthand",
                "object-literal-sort-keys",
                "one-line",
                "one-variable-per-declaration",
                "only-arrow-functions",
                "ordered-imports",
                "prefer-const",
                "prefer-for-of",
                "promise-function-async",
                "prefer-function-over-method",
                "prefer-method-signature",
                "quotemark",
                "radix",
                "restrict-plus-operands",
                "semicolon",
                "space-before-function-paren",
                "strict-boolean-expressions",
                "strict-type-predicates",
                "switch-default",
                "trailing-comma",
                "triple-equals",
                "typedef",
                "typedef-whitespace",
                "typeof-compare",
                "unified-signatures",
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
