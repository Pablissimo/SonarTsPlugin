package io.github.sleroy.sonar.model;

import org.junit.Test;
import org.sonar.api.rule.Severity;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.debt.DebtRemediationFunction;

import static org.junit.Assert.assertEquals;

public class EsLintRuleTest {
    @Test
    public void ruleWithoutDebtRemediation() {
        EsLintRule rule = new EsLintRule(
            "key",
            Severity.MAJOR,
            "name",
            "<html></html>",""
        );

        assertEquals("key", rule.getKey());
        assertEquals(Severity.MAJOR, rule.getSeverity());
        assertEquals("name", rule.getName());
        assertEquals("<html></html>", rule.getHtmlDescription());
        assertEquals(false, rule.isHasDebtRemediation());
        assertEquals(DebtRemediationFunction.Type.CONSTANT_ISSUE, rule.getDebtRemediationFunction());
        assertEquals("0min", rule.getDebtRemediationScalar());
        assertEquals("0min", rule.getDebtRemediationOffset());
        assertEquals(null, rule.getDebtType());
    }

    @Test
    public void ruleWithDebtRemediation() {
        EsLintRule rule = new EsLintRule(
            "key",
            Severity.MAJOR,
            "name",
            "<html></html>",
                DebtRemediationFunction.Type.LINEAR_OFFSET,
            "1min",
            "2min",
            RuleType.CODE_SMELL.name(),""
        );

        assertEquals("key", rule.getKey());
        assertEquals(Severity.MAJOR, rule.getSeverity());
        assertEquals("name", rule.getName());
        assertEquals("<html></html>", rule.getHtmlDescription());
        assertEquals(true, rule.isHasDebtRemediation());
        assertEquals(DebtRemediationFunction.Type.LINEAR_OFFSET, rule.getDebtRemediationFunction());
        assertEquals("1min", rule.getDebtRemediationScalar());
        assertEquals("2min", rule.getDebtRemediationOffset());
        assertEquals(RuleType.CODE_SMELL.name(), rule.getDebtType());
    }
}
