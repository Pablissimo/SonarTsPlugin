package com.pablissimo.sonar.model;

import static org.junit.Assert.*;

import java.lang.reflect.Array;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TsLintConfigTest {
	TsLintConfig model;
	
	@Before
	public void setUp() throws Exception {
		this.model = new TsLintConfig();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void getRules_does_not_return_null() {
		assertNotNull(this.model.getRules());
	}
	
	@Test
	public void addRule_with_boolean_creates_rule() {
		this.model.addRule("the rule", true);
		
		assertTrue(this.model.getRules().containsKey("the rule"));
		assertEquals(true, this.model.getRules().get("the rule"));
	}
	
	@Test
	public void addRule_with_objects_creates_rule() {
		this.model.addRule("the rule", 1, "string");
		
		assertTrue(this.model.getRules().containsKey("the rule"));

		Object[] ruleParams = (Object[]) this.model.getRules().get("the rule");
		
		assertNotNull(ruleParams);
		assertTrue(ruleParams[0].equals(1));
		assertTrue(ruleParams[1].equals("string"));
	}
}
