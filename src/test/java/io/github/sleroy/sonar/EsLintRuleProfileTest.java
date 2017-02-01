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
        this.validationMessages = ValidationMessages.create();
        this.ruleProfile = new EsLintRuleProfile();
        this.expectedRuleNames = new HashSet<String>(Arrays.asList(
                EsRulesDefinition.ESLINT_UNKNOWN_RULE.getKey(),
                "component-limit", "controller-as-route", "controller-as-vm", "controller-as", "deferred", "di-unused", "directive-restrict", "empty-controller", "no-controller", "no-inline-template", "no-run-logic", "no-services", "on-watch", "prefer-component", "no-cookiestore", "no-directive-replace", "no-http-callback", "component-name", "constant-name", "controller-name", "directive-name", "factory-name", "file-name", "filter-name", "module-name", "provider-name", "service-name", "value-name", "di-order", "di", "dumb-inject", "function-type", "module-dependency-order", "no-service-method", "one-dependency-per-line", "rest-service", "watchers-execution", "angularelement", "definedundefined", "document-service", "foreach", "interval-service", "json-functions", "log", "no-angular-mock", "no-jquery-angularelement", "timeout-service", "typecheck-array", "typecheck-date", "typecheck-function", "typecheck-number", "typecheck-object", "typecheck-string", "window-service", "on-destroy"

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
