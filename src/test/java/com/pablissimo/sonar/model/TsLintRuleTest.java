package com.pablissimo.sonar.model;

import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;
import org.sonar.api.server.rule.RulesDefinition;

import static org.junit.Assert.assertEquals;

public class TsLintRuleTest {
    @Test
    public void ruleWithoutDebtRemediation() {
        TsLintRule rule = new TsLintRule(
            "key",
            Severity.MAJOR,
            "name",
            "<html></html>"
        );

        assertEquals("key", rule.key);
        assertEquals(Severity.MAJOR, rule.severity);
        assertEquals("name", rule.name);
        assertEquals("<html></html>", rule.htmlDescription);
        assertEquals(false, rule.hasDebtRemediation);
        assertEquals(DebtRemediationFunction.Type.CONSTANT_ISSUE, rule.debtRemediationFunction);
        assertEquals("0min", rule.debtRemediationScalar);
        assertEquals("0min", rule.debtRemediationOffset);
        assertEquals(null, rule.debtType);
    }

    @Test
    public void ruleWithDebtRemediation() {
        TsLintRule rule = new TsLintRule(
            "key",
            Severity.MAJOR,
            "name",
            "<html></html>",
            DebtRemediationFunction.Type.LINEAR_OFFSET,
            "1min",
            "2min",
            RuleType.CODE_SMELL.name()
        );

        assertEquals("key", rule.key);
        assertEquals(Severity.MAJOR, rule.severity);
        assertEquals("name", rule.name);
        assertEquals("<html></html>", rule.htmlDescription);
        assertEquals(true, rule.hasDebtRemediation);
        assertEquals(DebtRemediationFunction.Type.LINEAR_OFFSET, rule.debtRemediationFunction);
        assertEquals("1min", rule.debtRemediationScalar);
        assertEquals("2min", rule.debtRemediationOffset);
        assertEquals(RuleType.CODE_SMELL.name(), rule.debtType);
    }
}
