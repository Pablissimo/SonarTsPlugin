package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;

public class TsRulesDefinition implements RulesDefinition {
	private static final Logger LOG = LoggerFactory.getLogger(TsRulesDefinition.class);
	
	public void define(Context context) {
		NewRepository repository = 
				context
				.createRepository("tslint", "ts")
				.setName("TsLint Analyser");
		
		repository.createRule("ban").setName("Use of this method is banned by current configuration").setSeverity(Severity.CRITICAL);
		repository.createRule("class-name").setName("Name must use PascalCase").setSeverity(Severity.MAJOR);
		
		NewRule commentFormatRule = repository.createRule("comment-format").setName("Comments must be correctly formatted").setSeverity(Severity.MINOR);
		commentFormatRule
			.createParam("check-sapce")
			.setDescription("enforces the rule that all single-line comments must begin with a space, as in // comment")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		commentFormatRule
			.createParam("check-lowercase")
			.setDescription("enforces the rule that the first non-whitespace character of a comment must be lowercase, if applicable")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("false");
		
		repository.createRule("curly").setName("enforces braces for if/for/do/while statements").setSeverity(Severity.MAJOR);
		repository.createRule("eofline").setName("enforces the file to end with a newline").setSeverity(Severity.MINOR);
		repository.createRule("forin").setName("enforces a for ... in statement to be filtered with an if statement").setSeverity(Severity.MAJOR);
		
		NewRule indentRule = repository.createRule("indent").setName("enforces consistent indentation with tabs or spaces").setSeverity(Severity.MINOR);
		indentRule
			.createParam("number-of-spaces")
			.setDescription("Number of spaces or tabs to be used for indentation")
			.setDefaultValue("4");
		
		repository.createRule("interface-name").setName("enforces the rule that interface names must begin with a capital I").setSeverity(Severity.MAJOR);
		repository.createRule("jsdoc-format").setName("enforces basic format rules for jsdoc comments - comments starting with /**").setSeverity(Severity.MAJOR);
		repository.createRule("label-position").setName("enforces labels only on sensible statements").setSeverity(Severity.MAJOR);
		repository.createRule("label-undefined").setName("checks that labels are defined before usage").setSeverity(Severity.CRITICAL);
		
		NewRule maxLineLengthRule = repository.createRule("max-line-length").setName("sets the maximum length of a line");
		maxLineLengthRule
			.createParam("max-line-length")
			.setDescription("Maximum allowed line length in characters")
			.setType(RuleParamType.INTEGER)
			.setDefaultValue("120");
		
		NewRule memberOrderingRule = repository.createRule("member-ordering").setName("enforces ordering of class members").setSeverity(Severity.MAJOR);
		memberOrderingRule
			.createParam("public-before-private")
			.setDescription("Require public members be defined before private members")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		memberOrderingRule
			.createParam("static-before-instance")
			.setDescription("Require static members be defined before instance members")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		memberOrderingRule
			.createParam("variables-before-functions")
			.setDescription("Require member variables to be defined before member functions")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		repository.createRule("no-any").setName("'any' must not be used as a type decoration").setSeverity(Severity.MAJOR);
		repository.createRule("no-arg").setName("arguments.callee must not be used").setSeverity(Severity.MAJOR);
		repository.createRule("no-bitwise").setName("bitwise operators must not be used").setSeverity(Severity.MAJOR);
		
		NewRule noConsoleRule = repository.createRule("no-console").setName("Specified function must not be called on the global console object").setSeverity(Severity.MAJOR);
		noConsoleRule
			.createParam("banned-console-functions")
			.setDescription("Comma-separated list of functions that should not be called on the console object")
			.setType(RuleParamType.STRING)
			.setDefaultValue(null);
		
		repository.createRule("no-consecutive-blank-lines").setName("No more than one blank line should appear in a row").setSeverity(Severity.MINOR);
		repository.createRule("no-construct").setName("Constructors of String, Number and Boolean must not be used").setSeverity(Severity.MAJOR);
		repository.createRule("no-constructor-vars").setName("Public and private modifiers must not be used on constructor arguments").setSeverity(Severity.MAJOR);
		repository.createRule("no-debugger").setName("Debugger statements are not allowed").setSeverity(Severity.CRITICAL);
		repository.createRule("no-duplicate-key").setName("Duplicate keys must not be specified in object literals").setSeverity(Severity.CRITICAL);
		repository.createRule("no-duplicate-variable").setName("Duplicate variable definitions are not allowed").setSeverity(Severity.MAJOR);
		repository.createRule("no-empty").setName("Empty blocks are not allowed").setSeverity(Severity.MAJOR);
		repository.createRule("no-eval").setName("Use of eval is not allowed").setSeverity(Severity.CRITICAL);
		repository.createRule("no-string-literal").setName("Object access via string literals is not allowed").setSeverity(Severity.MAJOR);
		repository.createRule("no-switch-case-fall-through").setName("Falling through one case statement to another is not allowed").setSeverity(Severity.MAJOR);
		repository.createRule("no-trailing-comma").setName("Trailing commas should not be used within object literals").setSeverity(Severity.MINOR);
		repository.createRule("no-trailing-whitespace").setName("Trailing whitespace at the end of lines is not allowed").setSeverity(Severity.MINOR);
		repository.createRule("no-unreachable").setName("Unreachable code after break, catch, throw and return statements is not allowed").setSeverity(Severity.MAJOR);
		repository.createRule("no-unused-expression").setName("Unused expressions (those that aren't assignments or function calls) are not allowed").setSeverity(Severity.MAJOR);
				
		NewRule noUnusedVariablesRule = repository.createRule("no-unused-variable").setName("Unused imports, variables, functions and private class members are not allowed").setSeverity(Severity.MAJOR);
		noUnusedVariablesRule
			.createParam("check-parameters")
			.setDescription("EXPERIMENTAL: Disallow unused function and constructor parameters")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("false");

		repository.createRule("no-use-before-declare").setName("Variable use before declaration is not allowed").setSeverity(Severity.CRITICAL);
		repository.createRule("no-var-requires").setName("Require is only allowed in import statements").setSeverity(Severity.MAJOR);
		
		NewRule oneLineRule = repository.createRule("one-line").setName("No newline is allowed before keyword").setSeverity(Severity.MINOR);
		oneLineRule
			.createParam("check-catch")
			.setDescription("Require catch statement on same line as try block closing brace")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		oneLineRule
			.createParam("check-else")
			.setDescription("Require else statement on same line as if statement closing brace")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		oneLineRule
			.createParam("check-open-brace")
			.setDescription("Require opening braces appear on the same line as preceding expression")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		oneLineRule
			.createParam("check-whitespace")
			.setDescription("Require whitespace between try/if block closing brace and catch/else keyword, and if condition and opening brace")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		NewRule quoteMarkRule = repository.createRule("quotemark").setName("Consistent use of single or double quotes is required - a mixture is not allowed").setSeverity(Severity.MAJOR);
		quoteMarkRule
			.createParam("quote-mark")
			.setDescription("Quotation mark that must be used")
			.setType(RuleParamType.singleListOfValues("single", "double"))
			.setDefaultValue("double");
		
		repository.createRule("radix").setName("A radix must be specified when calling parseInt").setSeverity(Severity.CRITICAL);
		repository.createRule("semicolon").setName("Statement must end with a semicolon").setSeverity(Severity.MAJOR);
		
		NewRule tripleEqualsRule = repository.createRule("triple-equals").setName("== and != must not be used - use === or !== instead").setSeverity(Severity.MAJOR);
		tripleEqualsRule
			.createParam("allow-null-check")
			.setDescription("Allow double-equals in null checks")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		NewRule typeDefRule = repository.createRule("typedef").setName("Type definition must be specified").setSeverity(Severity.MAJOR);
		typeDefRule
			.createParam("call-signature")
			.setDescription("Require function return types be specified")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		typeDefRule
			.createParam("index-signature")
			.setDescription("Require index type specifier for indexers")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		typeDefRule
			.createParam("parameter")
			.setDescription("Require parameter types to be specified")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		typeDefRule
			.createParam("property-signature")
			.setDescription("Require interface property types to be specified")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		typeDefRule
			.createParam("variable-declarator")
			.setDescription("Require variable types be specified")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		typeDefRule
			.createParam("member-variable-declaractor")
			.setDescription("Require member variable types be specified")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		NewRule typeDefWhitespaceRule = repository.createRule("typedef-whitespace").setName("Whitespace around type definitions must be correct").setSeverity(Severity.MINOR);
		typeDefWhitespaceRule
			.createParam("call-signature")
			.setDescription("Configure whitespace before function parameter list and return type specification")
			.setType(RuleParamType.singleListOfValues("space", "noSpace"))
			.setDefaultValue("noSpace");
		
		typeDefWhitespaceRule
			.createParam("catch-clause")
			.setDescription("Configure whitespace before exception type specifier in catch clauses")
			.setType(RuleParamType.singleListOfValues("space", "noSpace"))
			.setDefaultValue("noSpace");
		
		typeDefWhitespaceRule
			.createParam("index-signature")
			.setDescription("Configure whitespace before index-type specifier in indexers")
			.setType(RuleParamType.singleListOfValues("space", "noSpace"))
			.setDefaultValue("space");
		
		NewRule useStrictRule = repository.createRule("use-strict").setName("Strict mode must be used").setSeverity(Severity.CRITICAL);
		useStrictRule
			.createParam("check-module")
			.setDescription("Check that top-level modules use strict mode")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		useStrictRule
			.createParam("check-function")
			.setDescription("Check that top-level functions use strict mode")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		NewRule variableNameRule = repository.createRule("variable-name").setName("Variable names must be either camelCased or UPPER_CASED").setSeverity(Severity.MAJOR);
		variableNameRule
			.createParam("allow-leading-underscore")
			.setDescription("Allow variable names to begin with an underscore")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		NewRule whitespaceRule = repository.createRule("whitespace").setName("Inappropriate whitespace between tokens").setSeverity(Severity.MINOR);
		whitespaceRule
			.createParam("check-branch")
			.setDescription("Branch and loop statements (if/else/for/while) must be followed by whitespace")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		whitespaceRule
			.createParam("check-decl")
			.setDescription("Variable declarations must have whitespace around the equals sign")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		whitespaceRule
			.createParam("check-operator")
			.setDescription("Whitespace is required around operators")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		whitespaceRule
			.createParam("check-separator")
			.setDescription("Whitespace is required after separator tokens (comma and semicolon)")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		whitespaceRule
			.createParam("check-type")
			.setDescription("Whitespace is required before a type specification")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");
		
		whitespaceRule
			.createParam("check-typecast")
			.setDescription("Whitespace is required between a typecast and its target")
			.setType(RuleParamType.BOOLEAN)
			.setDefaultValue("true");

		for (NewRule rule : repository.rules()) {
			rule.setHtmlDescription("HTML description to follow");
			rule.setStatus(RuleStatus.READY);
		}
		
		repository.done();
	}
}
