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
                "angular-component-limit", "angular-controller-as-route", "angular-controller-as-vm", "angular-controller-as", "angular-deferred", "angular-di-unused", "angular-directive-restrict", "angular-empty-controller", "angular-no-controller", "angular-no-inline-template", "angular-no-run-logic", "angular-no-services", "angular-on-watch", "angular-prefer-component", "angular-no-cookiestore", "angular-no-directive-replace", "angular-no-http-callback", "angular-component-name", "angular-constant-name", "angular-controller-name", "angular-directive-name", "angular-factory-name", "angular-file-name", "angular-filter-name", "angular-module-name", "angular-provider-name", "angular-service-name", "angular-value-name", "angular-di-order", "angular-di", "angular-dumb-inject", "angular-function-type", "angular-module-dependency-order", "angular-no-service-method", "angular-one-dependency-per-line", "angular-rest-service", "angular-watchers-execution", "angular-angularelement", "angular-definedundefined", "angular-document-service", "angular-foreach", "angular-interval-service", "angular-json-functions", "angular-log", "angular-no-angular-mock", "angular-no-jquery-angularelement", "angular-timeout-service", "angular-typecheck-array", "angular-typecheck-date", "angular-typecheck-function", "angular-typecheck-number", "angular-typecheck-object", "angular-typecheck-string", "angular-window-service", "angular-on-destroy"
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
