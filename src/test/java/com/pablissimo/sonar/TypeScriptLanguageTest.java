package com.pablissimo.sonar;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TypeScriptLanguageTest {
    TypeScriptLanguage language;

    @Before
    public void setUp() throws Exception {
        this.language = new TypeScriptLanguage();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void identifiesCorrectly() {
        assertEquals("TypeScript", this.language.getName());
        assertEquals("ts", this.language.getKey());
    }

    @Test
    public void definesCorrectExtension() {
        String[] suffices = this.language.getFileSuffixes();

        assertEquals(1, suffices.length);
        assertEquals("ts", suffices[0]);
    }
}
