package io.github.sleroy.sonar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.utils.ValidationMessages;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class EsLintRuleProfileTest {
    ValidationMessages validationMessages;
    EsLintRuleProfile ruleProfile;

    HashSet<String> expectedRuleNames;

    @Before
    public void setUp() throws Exception {
        validationMessages = ValidationMessages.create();
        ruleProfile = new EsLintRuleProfile();
        expectedRuleNames = new HashSet<String>(Arrays.asList(
                EsRulesDefinition.ESLINT_UNKNOWN_RULE.getKey(),
                "angular-module-getter","angular-module-setter","angular-no-private-call",
                "no-await-in-loop", "no-cond-assign", "no-console", "no-constant-condition", "no-control-regex", "no-debugger", "no-dupe-args", "no-dupe-keys", "no-duplicate-case", "no-empty-character-class", "no-empty", "no-ex-assign", "no-extra-boolean-cast", "no-extra-parens", "no-extra-semi", "no-func-assign", "no-inner-declarations", "no-invalid-regexp", "no-irregular-whitespace", "no-obj-calls", "no-prototype-builtins", "no-regex-spaces", "no-sparse-arrays", "no-template-curly-in-string", "no-unexpected-multiline", "no-unreachable", "no-unsafe-finally", "no-unsafe-negation", "use-isnan", "valid-jsdoc", "valid-typeof", "accessor-pairs", "array-callback-return", "block-scoped-var", "class-methods-use-this", "complexity", "consistent-return", "curly", "default-case", "dot-location", "dot-notation", "eqeqeq", "guard-for-in", "no-alert", "no-caller", "no-case-declarations", "no-div-regex", "no-else-return", "no-empty-function", "no-empty-pattern", "no-eq-null", "no-eval", "no-extend-native", "no-extra-bind", "no-extra-label", "no-fallthrough", "no-floating-decimal", "no-global-assign", "no-implicit-coercion", "no-implicit-globals", "no-implied-eval", "no-invalid-this", "no-iterator", "no-labels", "no-lone-blocks", "no-loop-func", "no-magic-numbers", "no-multi-spaces", "no-multi-str", "no-new-func", "no-new-wrappers", "no-new", "no-octal-escape", "no-octal", "no-param-reassign", "no-proto", "no-redeclare", "no-restricted-properties", "no-return-assign", "no-return-await", "no-script-url", "no-self-assign", "no-self-compare", "no-sequences", "no-throw-literal", "no-unmodified-loop-condition", "no-unused-expressions", "no-unused-labels", "no-useless-call", "no-useless-concat", "no-useless-escape", "no-useless-return", "no-void", "no-warning-comments", "no-with", "prefer-promise-reject-errors", "radix", "require-await", "vars-on-top", "wrap-iife", "yoda", "strict", "init-declarations", "no-catch-shadow", "no-delete-var", "no-label-var", "no-restricted-globals", "no-shadow-restricted-names", "no-shadow", "no-undef-init", "no-undef", "no-undefined", "no-unused-vars", "no-use-before-define", "callback-return", "global-require", "handle-callback-err", "no-mixed-requires", "no-new-require", "no-path-concat", "no-process-env", "no-process-exit", "no-restricted-modules", "no-sync", "array-bracket-spacing", "block-spacing", "brace-style", "camelcase", "capitalized-comments", "comma-dangle", "comma-spacing", "comma-style", "computed-property-spacing", "consistent-this", "eol-last", "func-call-spacing", "func-name-matching", "func-names", "func-style", "id-blacklist", "id-length", "id-match", "indent", "jsx-quotes", "key-spacing", "keyword-spacing", "line-comment-position", "linebreak-style", "lines-around-comment", "lines-around-directive", "max-depth", "max-len", "max-lines", "max-nested-callbacks", "max-params", "max-statements-per-line", "max-statements", "multiline-ternary", "new-cap", "new-parens", "newline-after-var", "newline-before-return", "newline-per-chained-call", "no-array-constructor", "no-bitwise", "no-continue", "no-inline-comments", "no-lonely-if", "no-mixed-operators", "no-mixed-spaces-and-tabs", "no-multi-assign", "no-multiple-empty-lines", "no-negated-condition", "no-nested-ternary", "no-new-object", "no-plusplus", "no-restricted-syntax", "no-tabs", "no-ternary", "no-trailing-spaces", "no-underscore-dangle", "no-unneeded-ternary", "no-whitespace-before-property", "object-curly-newline", "object-curly-spacing", "object-property-newline", "one-var-declaration-per-line", "one-var", "operator-assignment", "operator-linebreak", "padded-blocks", "quote-props", "quotes", "require-jsdoc", "semi-spacing", "semi", "sort-keys", "sort-vars", "space-before-blocks", "space-before-function-paren", "space-in-parens", "space-infix-ops", "space-unary-ops", "spaced-comment", "unicode-bom", "wrap-regex", "arrow-body-style", "arrow-parens", "arrow-spacing", "constructor-super", "generator-star-spacing", "no-class-assign", "no-confusing-arrow", "no-const-assign", "no-dupe-class-members", "no-duplicate-imports", "no-new-symbol", "no-restricted-imports", "no-this-before-super", "no-useless-computed-key", "no-useless-constructor", "no-useless-rename", "no-var", "object-shorthand", "prefer-arrow-callback", "prefer-const", "prefer-destructuring", "prefer-numeric-literals", "prefer-rest-params", "prefer-spread", "prefer-template", "require-yield", "rest-spread-spacing", "sort-imports", "symbol-description", "template-curly-spacing", "yield-star-spacing"
                , "angular-component-limit", "angular-controller-as-route", "angular-controller-as-vm", "angular-controller-as", "angular-deferred", "angular-di-unused", "angular-directive-restrict", "angular-empty-controller", "angular-no-controller", "angular-no-inline-template", "angular-no-run-logic", "angular-no-services", "angular-on-watch", "angular-prefer-component", "angular-no-cookiestore", "angular-no-directive-replace", "angular-no-http-callback", "angular-component-name", "angular-constant-name", "angular-controller-name", "angular-directive-name", "angular-factory-name", "angular-file-name", "angular-filter-name", "angular-module-name", "angular-provider-name", "angular-service-name", "angular-value-name", "angular-di-order", "angular-di", "angular-dumb-inject", "angular-function-type", "angular-module-dependency-order", "angular-no-service-method", "angular-one-dependency-per-line", "angular-rest-service", "angular-watchers-execution", "angular-angularelement", "angular-definedundefined", "angular-document-service", "angular-foreach", "angular-interval-service", "angular-json-functions", "angular-log", "angular-no-angular-mock", "angular-no-jquery-angularelement", "angular-timeout-service", "angular-typecheck-array", "angular-typecheck-date", "angular-typecheck-function", "angular-typecheck-number", "angular-typecheck-object", "angular-typecheck-string", "angular-window-service", "angular-on-destroy"
        ));
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void definesExpectedRules() {
        RulesProfile profile = ruleProfile.createProfile(validationMessages);

        for (String ruleName : expectedRuleNames) {
            assertNotNull("Expected rule missing in plugin: " + ruleName, profile.getActiveRule(EsRulesDefinition.REPOSITORY_NAME, ruleName));
        }
    }

    @Test
    public void definesUnexpectedRules() {
        RulesProfile profile = ruleProfile.createProfile(validationMessages);

        for (ActiveRule rule : profile.getActiveRules()) {
            assertTrue("Unexpected rule in plugin: " + rule.getRuleKey(), expectedRuleNames.contains(rule.getRuleKey()));
        }
    }
}
