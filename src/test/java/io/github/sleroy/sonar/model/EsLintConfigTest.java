package io.github.sleroy.sonar.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class EsLintConfigTest {
    EsLintConfig model;

    @Before
    public void setUp() throws Exception {
        model = new EsLintConfig();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getRules_DoesNotReturnNull() {
        assertNotNull(model.getRules());
    }

    @Test
    public void addRule_WithBoolean_CreatesRule() {
        model.addEnabledRule("the rule");

        assertTrue(model.getRules().containsKey("the rule"));
        assertEquals(true, model.getRules().get("the rule"));
    }

    @Test
    public void addRule_WithObjects_CreatesRule() {
        model.addRuleWithArgs("the rule", 1, "string");

        assertTrue(model.getRules().containsKey("the rule"));

        Object[] ruleParams = (Object[]) model.getRules().get("the rule");

        assertNotNull(ruleParams);
        assertTrue(ruleParams[0].equals(1));
        assertTrue(ruleParams[1].equals("string"));
    }
}
