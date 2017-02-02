package io.github.sleroy.sonar.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EsLintIssueTest {
    EsLintIssue model;

    @Before
    public void setUp() throws Exception {
        model = new EsLintIssue();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSetName() {
        model.setName("the file");
        assertEquals("the file", model.getName());
    }


    @Test
    public void getSetFailure() {
        model.setMessage("the failure");
        assertEquals("the failure", model.getMessage());
    }

    @Test
    public void getSetRuleName() {
        model.setRuleId("the rule");
        assertEquals("the rule", model.getRuleId());
    }
}
