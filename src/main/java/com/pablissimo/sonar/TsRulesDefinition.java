package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.server.rule.RulesDefinition;

public class TsRulesDefinition implements RulesDefinition {
	private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);
	
	public void define(Context context) {
		NewRepository repo = context.createRepository("tslint", "ts").setName("TsLint Analyser");
		
		String rules = "ban,class-name,comment-format,curly,eofline,forin,indent,interface-name,jsdoc-format,label-position,label-undefined,max-line-length,member-ordering,no-arg,no-bitwise,no-console,no-construct,no-constructor-vars,no-debugger,no-duplicate-key,no-duplicate-variable,no-empty,no-eval,no-string-literal,no-switch-case-fall-through,no-trailing-comma,no-trailing-whitespace,no-unused-expression,no-unused-variable,no-unreachable,no-use-before-declare,no-var-requires,one-line,quotemark,radix,semicolon,triple-equals,typedef,typedef-whitespace,use-strict,variable-name,whitespace";
		String[] rulesList = rules.split(",");
		
		for (int i = 0; i < rulesList.length; i++) {
			repo.createRule(rulesList[i]).setName(rulesList[i]).setHtmlDescription(rulesList[i]).setStatus(RuleStatus.READY);
		}
		
		repo.done();
		
		LOG.info("Created " + repo.rules().size() + " rules for TsLint");
	}
}
