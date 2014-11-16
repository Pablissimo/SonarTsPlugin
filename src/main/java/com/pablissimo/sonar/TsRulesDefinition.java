package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.server.rule.RulesDefinition;

public class TsRulesDefinition implements RulesDefinition {
	private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);
	
	public void define(Context context) {
		NewRepository repo = context.createRepository("TsLint", "ts").setName("TsLint Analyser");
		repo.createRule("TestRule").setName("Test rule name").setHtmlDescription("Description");
		
		repo.done();
		
		LOG.info("Created " + repo.rules().size() + " rules for TsLint");
	}
}
