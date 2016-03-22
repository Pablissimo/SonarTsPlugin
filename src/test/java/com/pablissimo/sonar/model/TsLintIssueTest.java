package com.pablissimo.sonar.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TsLintIssueTest {
    TsLintIssue model;

    @Before
    public void setUp() throws Exception {
        this.model = new TsLintIssue();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSetName() {
        this.model.setName("the file");
        assertEquals("the file", this.model.getName());
    }

    @Test
    public void getSetStartPosition() {
        TsLintPosition startPosition = new TsLintPosition();
        this.model.setStartPosition(startPosition);
        assertEquals(startPosition, this.model.getStartPosition());
    }

    @Test
    public void getSetEndPosition() {
        TsLintPosition endPosition = new TsLintPosition();
        this.model.setEndPosition(endPosition);
        assertEquals(endPosition, this.model.getEndPosition());
    }

    @Test
    public void getSetFailure() {
        this.model.setFailure("the failure");
        assertEquals("the failure", this.model.getFailure());
    }

    @Test
    public void getSetRuleName() {
        this.model.setRuleName("the rule");
        assertEquals("the rule", this.model.getRuleName());
    }
}
