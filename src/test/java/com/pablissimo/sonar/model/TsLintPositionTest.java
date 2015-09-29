package com.pablissimo.sonar.model;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TsLintPositionTest {
    TsLintPosition model;

    @Before
    public void setUp() throws Exception {
        this.model = new TsLintPosition();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getSetPosition() {
        this.model.setPosition(123);
        assertEquals(123, this.model.getPosition());
    }

    @Test
    public void getSetLine() {
        this.model.setLine(234);
        assertEquals(234, this.model.getLine());
    }

    @Test
    public void getSetCharacter() {
        this.model.setCharacter(345);
        assertEquals(345, this.model.getCharacter());
    }
}
