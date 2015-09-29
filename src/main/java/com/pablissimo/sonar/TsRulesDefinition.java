package com.pablissimo.sonar;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RuleParamType;
import org.sonar.api.server.rule.RulesDefinition;

public class TsRulesDefinition implements RulesDefinition {
    public static final String RULE_TRIPLE_EQUALS = "triple-equals";
    public static final String RULE_PARAM_CHECK_TYPECAST = "check-typecast";
    public static final String RULE_PARAM_CHECK_TYPE = "check-type";
    public static final String RULE_PARAM_CHECK_SEPARATOR = "check-separator";
    public static final String RULE_PARAM_CHECK_OPERATOR = "check-operator";
    public static final String RULE_PARAM_CHECK_DECL = "check-decl";
    public static final String RULE_PARAM_CHECK_BRANCH = "check-branch";
    public static final String RULE_WHITESPACE = "whitespace";
    public static final String RULE_PARAM_ALLOW_LEADING_UNDERSCORE = "allow-leading-underscore";
    public static final String RULE_VARIABLE_NAME = "variable-name";
    public static final String RULE_PARAM_CHECK_FUNCTION = "check-function";
    public static final String RULE_PARAM_CHECK_MODULE = "check-module";
    public static final String RULE_USE_STRICT = "use-strict";
    public static final String RULE_PARAM_CATCH_CLAUSE = "catch-clause";
    public static final String RULE_TYPEDEF_WHITESPACE = "typedef-whitespace";
    public static final String RULE_PARAM_MEMBER_VARIABLE_DECLARACTOR = "member-variable-declaractor";
    public static final String RULE_PARAM_VARIABLE_DECLARATOR = "variable-declarator";
    public static final String RULE_PARAM_PROPERTY_SIGNATURE = "property-signature";
    public static final String RULE_PARAM_PARAMETER = "parameter";
    public static final String RULE_PARAM_INDEX_SIGNATURE = "index-signature";
    public static final String RULE_PARAM_CALL_SIGNATURE = "call-signature";
    public static final String RULE_TYPEDEF = "typedef";
    public static final String RULE_PARAM_ALLOW_NULL_CHECK = "allow-null-check";
    public static final String RULE_SEMICOLON = "semicolon";
    public static final String RULE_RADIX = "radix";
    public static final String RULE_PARAM_QUOTE_MARK = "quote-mark";
    public static final String RULE_QUOTEMARK = "quotemark";
    public static final String RULE_PARAM_CHECK_WHITESPACE = "check-whitespace";
    public static final String RULE_PARAM_CHECK_OPEN_BRACE = "check-open-brace";
    public static final String RULE_PARAM_CHECK_ELSE = "check-else";
    public static final String RULE_PARAM_CHECK_CATCH = "check-catch";
    public static final String RULE_ONE_LINE = "one-line";
    public static final String RULE_NO_VAR_REQUIRES = "no-var-requires";
    public static final String RULE_NO_USE_BEFORE_DECLARE = "no-use-before-declare";
    public static final String RULE_PARAM_CHECK_PARAMETERS = "check-parameters";
    public static final String RULE_NO_UNUSED_VARIABLE = "no-unused-variable";
    public static final String RULE_NO_UNUSED_EXPRESSION = "no-unused-expression";
    public static final String RULE_NO_UNREACHABLE = "no-unreachable";
    public static final String RULE_NO_TRAILING_WHITESPACE = "no-trailing-whitespace";
    public static final String RULE_NO_TRAILING_COMMA = "no-trailing-comma";
    public static final String RULE_NO_SWITCH_CASE_FALL_THROUGH = "no-switch-case-fall-through";
    public static final String RULE_NO_STRING_LITERAL = "no-string-literal";
    public static final String RULE_NO_EVAL = "no-eval";
    public static final String RULE_NO_EMPTY = "no-empty";
    public static final String RULE_NO_DUPLICATE_VARIABLE = "no-duplicate-variable";
    public static final String RULE_NO_DUPLICATE_KEY = "no-duplicate-key";
    public static final String RULE_NO_DEBUGGER = "no-debugger";
    public static final String RULE_NO_CONSTRUCTOR_VARS = "no-constructor-vars";
    public static final String RULE_NO_CONSTRUCT = "no-construct";
    public static final String RULE_NO_CONSECUTIVE_BLANK_LINES = "no-consecutive-blank-lines";
    public static final String RULE_PARAM_BANNED_CONSOLE_FUNCTIONS = "banned-console-functions";
    public static final String RULE_NO_CONSOLE = "no-console";
    public static final String RULE_NO_BITWISE = "no-bitwise";
    public static final String RULE_NO_ARG = "no-arg";
    public static final String RULE_NO_ANY = "no-any";
    public static final String RULE_PARAM_VARIABLES_BEFORE_FUNCTIONS = "variables-before-functions";
    public static final String RULE_PARAM_STATIC_BEFORE_INSTANCE = "static-before-instance";
    public static final String RULE_PARAM_PUBLIC_BEFORE_PRIVATE = "public-before-private";
    public static final String RULE_MEMBER_ORDERING = "member-ordering";
    public static final String RULE_PARAM_MAX_LINE_LENGTH = "max-line-length";
    public static final String RULE_MAX_LINE_LENGTH = "max-line-length";
    public static final String RULE_LABEL_UNDEFINED = "label-undefined";
    public static final String RULE_LABEL_POSITION = "label-position";
    public static final String RULE_JSDOC_FORMAT = "jsdoc-format";
    public static final String RULE_INTERFACE_NAME = "interface-name";
    public static final String RULE_PARAM_TABS_OR_SPACES = "tabs-or-spaces";
    public static final String RULE_INDENT = "indent";
    public static final String RULE_FORIN = "forin";
    public static final String RULE_EOFLINE = "eofline";
    public static final String RULE_CURLY = "curly";
    public static final String RULE_PARAM_CHECK_LOWERCASE = "check-lowercase";
    public static final String RULE_PARAM_CHECK_SPACE = "check-space";
    public static final String RULE_COMMENT_FORMAT = "comment-format";
    public static final String RULE_CLASS_NAME = "class-name";
    public static final String RULE_BAN = "ban";

    public static final String REPOSITORY_NAME = "tslint";

    public void define(Context context) {
        NewRepository repository =
                context
                .createRepository(REPOSITORY_NAME, TypeScriptLanguage.LANGUAGE_EXTENSION)
                .setName("TsLint Analyser");

        repository.createRule(RULE_BAN).setName("Use of this method is banned by current configuration").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_CLASS_NAME).setName("Name must use PascalCase").setSeverity(Severity.MAJOR);

        NewRule commentFormatRule = repository.createRule(RULE_COMMENT_FORMAT).setName("Comments must be correctly formatted").setSeverity(Severity.MINOR);
        commentFormatRule
            .createParam(RULE_PARAM_CHECK_SPACE)
            .setDescription("enforces the rule that all single-line comments must begin with a space, as in // comment")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        commentFormatRule
            .createParam(RULE_PARAM_CHECK_LOWERCASE)
            .setDescription("enforces the rule that the first non-whitespace character of a comment must be lowercase, if applicable")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("false");

        repository.createRule(RULE_CURLY).setName("enforces braces for if/for/do/while statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_EOFLINE).setName("enforces the file to end with a newline").setSeverity(Severity.MINOR);
        repository.createRule(RULE_FORIN).setName("enforces a for ... in statement to be filtered with an if statement").setSeverity(Severity.MAJOR);

        NewRule indentRule = repository.createRule(RULE_INDENT).setName("enforces consistent indentation with tabs or spaces").setSeverity(Severity.MINOR);
        indentRule
            .createParam(RULE_PARAM_TABS_OR_SPACES)
            .setDescription("Specifies if tabs or spaces should be used for indentation")
            .setType(RuleParamType.singleListOfValues("spaces", "tabs"))
            .setDefaultValue("tabs");

        repository.createRule(RULE_INTERFACE_NAME).setName("enforces the rule that interface names must begin with a capital I").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_JSDOC_FORMAT).setName("enforces basic format rules for jsdoc comments - comments starting with /**").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_LABEL_POSITION).setName("enforces labels only on sensible statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_LABEL_UNDEFINED).setName("checks that labels are defined before usage").setSeverity(Severity.CRITICAL);

        NewRule maxLineLengthRule = repository.createRule(RULE_MAX_LINE_LENGTH).setName("sets the maximum length of a line").setSeverity(Severity.MAJOR);
        maxLineLengthRule
            .createParam(RULE_PARAM_MAX_LINE_LENGTH)
            .setDescription("Maximum allowed line length in characters")
            .setType(RuleParamType.INTEGER)
            .setDefaultValue("120");

        NewRule memberOrderingRule = repository.createRule(RULE_MEMBER_ORDERING).setName("enforces ordering of class members").setSeverity(Severity.MAJOR);
        memberOrderingRule
            .createParam(RULE_PARAM_PUBLIC_BEFORE_PRIVATE)
            .setDescription("Require public members be defined before private members")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        memberOrderingRule
            .createParam(RULE_PARAM_STATIC_BEFORE_INSTANCE)
            .setDescription("Require static members be defined before instance members")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        memberOrderingRule
            .createParam(RULE_PARAM_VARIABLES_BEFORE_FUNCTIONS)
            .setDescription("Require member variables to be defined before member functions")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        repository.createRule(RULE_NO_ANY).setName("'any' must not be used as a type decoration").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_ARG).setName("arguments.callee must not be used").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_BITWISE).setName("bitwise operators must not be used").setSeverity(Severity.MAJOR);

        NewRule noConsoleRule = repository.createRule(RULE_NO_CONSOLE).setName("Specified function must not be called on the global console object").setSeverity(Severity.MAJOR);
        noConsoleRule
            .createParam(RULE_PARAM_BANNED_CONSOLE_FUNCTIONS)
            .setDescription("Comma-separated list of functions that should not be called on the console object")
            .setType(RuleParamType.STRING)
            .setDefaultValue(null);

        repository.createRule(RULE_NO_CONSECUTIVE_BLANK_LINES).setName("No more than one blank line should appear in a row").setSeverity(Severity.MINOR);
        repository.createRule(RULE_NO_CONSTRUCT).setName("Constructors of String, Number and Boolean must not be used").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_CONSTRUCTOR_VARS).setName("Public and private modifiers must not be used on constructor arguments").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_DEBUGGER).setName("Debugger statements are not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_DUPLICATE_KEY).setName("Duplicate keys must not be specified in object literals").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_DUPLICATE_VARIABLE).setName("Duplicate variable definitions are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_EMPTY).setName("Empty blocks are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_EVAL).setName("Use of eval is not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_STRING_LITERAL).setName("Object access via string literals is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_SWITCH_CASE_FALL_THROUGH).setName("Falling through one case statement to another is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_TRAILING_COMMA).setName("Trailing commas should not be used within object literals").setSeverity(Severity.MINOR);
        repository.createRule(RULE_NO_TRAILING_WHITESPACE).setName("Trailing whitespace at the end of lines is not allowed").setSeverity(Severity.MINOR);
        repository.createRule(RULE_NO_UNREACHABLE).setName("Unreachable code after break, catch, throw and return statements is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_UNUSED_EXPRESSION).setName("Unused expressions (those that aren't assignments or function calls) are not allowed").setSeverity(Severity.MAJOR);

        NewRule noUnusedVariablesRule = repository.createRule(RULE_NO_UNUSED_VARIABLE).setName("Unused imports, variables, functions and private class members are not allowed").setSeverity(Severity.MAJOR);
        noUnusedVariablesRule
            .createParam(RULE_PARAM_CHECK_PARAMETERS)
            .setDescription("EXPERIMENTAL: Disallow unused function and constructor parameters")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("false");

        repository.createRule(RULE_NO_USE_BEFORE_DECLARE).setName("Variable use before declaration is not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_VAR_REQUIRES).setName("Require is only allowed in import statements").setSeverity(Severity.MAJOR);

        NewRule oneLineRule = repository.createRule(RULE_ONE_LINE).setName("No newline is allowed before keyword").setSeverity(Severity.MINOR);
        oneLineRule
            .createParam(RULE_PARAM_CHECK_CATCH)
            .setDescription("Require catch statement on same line as try block closing brace")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        oneLineRule
            .createParam(RULE_PARAM_CHECK_ELSE)
            .setDescription("Require else statement on same line as if statement closing brace")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        oneLineRule
            .createParam(RULE_PARAM_CHECK_OPEN_BRACE)
            .setDescription("Require opening braces appear on the same line as preceding expression")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        oneLineRule
            .createParam(RULE_PARAM_CHECK_WHITESPACE)
            .setDescription("Require whitespace between try/if block closing brace and catch/else keyword, and if condition and opening brace")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        NewRule quoteMarkRule = repository.createRule(RULE_QUOTEMARK).setName("Consistent use of single or double quotes is required - a mixture is not allowed").setSeverity(Severity.MAJOR);
        quoteMarkRule
            .createParam(RULE_PARAM_QUOTE_MARK)
            .setDescription("Quotation mark that must be used")
            .setType(RuleParamType.singleListOfValues("single", "double"))
            .setDefaultValue("double");

        repository.createRule(RULE_RADIX).setName("A radix must be specified when calling parseInt").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_SEMICOLON).setName("Statement must end with a semicolon").setSeverity(Severity.MAJOR);

        NewRule tripleEqualsRule = repository.createRule(RULE_TRIPLE_EQUALS).setName("== and != must not be used - use === or !== instead").setSeverity(Severity.MAJOR);
        tripleEqualsRule
            .createParam(RULE_PARAM_ALLOW_NULL_CHECK)
            .setDescription("Allow double-equals in null checks")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        NewRule typeDefRule = repository.createRule(RULE_TYPEDEF).setName("Type definition must be specified").setSeverity(Severity.MAJOR);
        typeDefRule
            .createParam(RULE_PARAM_CALL_SIGNATURE)
            .setDescription("Require function return types be specified")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        typeDefRule
            .createParam(RULE_PARAM_INDEX_SIGNATURE)
            .setDescription("Require index type specifier for indexers")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        typeDefRule
            .createParam(RULE_PARAM_PARAMETER)
            .setDescription("Require parameter types to be specified")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        typeDefRule
            .createParam(RULE_PARAM_PROPERTY_SIGNATURE)
            .setDescription("Require interface property types to be specified")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        typeDefRule
            .createParam(RULE_PARAM_VARIABLE_DECLARATOR)
            .setDescription("Require variable types be specified")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        typeDefRule
            .createParam(RULE_PARAM_MEMBER_VARIABLE_DECLARACTOR)
            .setDescription("Require member variable types be specified")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        NewRule typeDefWhitespaceRule = repository.createRule(RULE_TYPEDEF_WHITESPACE).setName("Whitespace around type definitions must be correct").setSeverity(Severity.MINOR);
        typeDefWhitespaceRule
            .createParam(RULE_PARAM_CALL_SIGNATURE)
            .setDescription("Configure whitespace before function parameter list and return type specification")
            .setType(RuleParamType.singleListOfValues("space", "noSpace"))
            .setDefaultValue("noSpace");

        typeDefWhitespaceRule
            .createParam(RULE_PARAM_CATCH_CLAUSE)
            .setDescription("Configure whitespace before exception type specifier in catch clauses")
            .setType(RuleParamType.singleListOfValues("space", "noSpace"))
            .setDefaultValue("noSpace");

        typeDefWhitespaceRule
            .createParam(RULE_PARAM_INDEX_SIGNATURE)
            .setDescription("Configure whitespace before index-type specifier in indexers")
            .setType(RuleParamType.singleListOfValues("space", "noSpace"))
            .setDefaultValue("space");

        NewRule useStrictRule = repository.createRule(RULE_USE_STRICT).setName("Strict mode must be used").setSeverity(Severity.CRITICAL);
        useStrictRule
            .createParam(RULE_PARAM_CHECK_MODULE)
            .setDescription("Check that top-level modules use strict mode")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        useStrictRule
            .createParam(RULE_PARAM_CHECK_FUNCTION)
            .setDescription("Check that top-level functions use strict mode")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        NewRule variableNameRule = repository.createRule(RULE_VARIABLE_NAME).setName("Variable names must be either camelCased or UPPER_CASED").setSeverity(Severity.MAJOR);
        variableNameRule
            .createParam(RULE_PARAM_ALLOW_LEADING_UNDERSCORE)
            .setDescription("Allow variable names to begin with an underscore")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        NewRule whitespaceRule = repository.createRule(RULE_WHITESPACE).setName("Inappropriate whitespace between tokens").setSeverity(Severity.MINOR);
        whitespaceRule
            .createParam(RULE_PARAM_CHECK_BRANCH)
            .setDescription("Branch and loop statements (if/else/for/while) must be followed by whitespace")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        whitespaceRule
            .createParam(RULE_PARAM_CHECK_DECL)
            .setDescription("Variable declarations must have whitespace around the equals sign")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        whitespaceRule
            .createParam(RULE_PARAM_CHECK_OPERATOR)
            .setDescription("Whitespace is required around operators")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        whitespaceRule
            .createParam(RULE_PARAM_CHECK_SEPARATOR)
            .setDescription("Whitespace is required after separator tokens (comma and semicolon)")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        whitespaceRule
            .createParam(RULE_PARAM_CHECK_TYPE)
            .setDescription("Whitespace is required before a type specification")
            .setType(RuleParamType.BOOLEAN)
            .setDefaultValue("true");

        whitespaceRule
            .createParam(RULE_PARAM_CHECK_TYPECAST)
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
