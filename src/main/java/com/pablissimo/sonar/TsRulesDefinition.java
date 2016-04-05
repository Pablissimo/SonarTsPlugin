package com.pablissimo.sonar;

import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rule.Severity;
import org.sonar.api.server.rule.RulesDefinition;

public class TsRulesDefinition implements RulesDefinition {
    public static final String REPOSITORY_NAME = "tslint";

    public static final String RULE_TSLINT_ISSUE = "tslint-issue";

    public static final String RULE_ALIGN = "align";
    public static final String RULE_BAN = "ban";
    public static final String RULE_CLASS_NAME = "class-name";
    public static final String RULE_COMMENT_FORMAT = "comment-format";
    public static final String RULE_CURLY = "curly";
    public static final String RULE_EOFLINE = "eofline";
    public static final String RULE_FORIN = "forin";
    public static final String RULE_INDENT = "indent";
    public static final String RULE_INTERFACE_NAME = "interface-name";
    public static final String RULE_JSDOC_FORMAT = "jsdoc-format";
    public static final String RULE_LABEL_POSITION = "label-position";
    public static final String RULE_LABEL_UNDEFINED = "label-undefined";
    public static final String RULE_MAX_LINE_LENGTH = "max-line-length";
    public static final String RULE_MEMBER_ACCESS = "member-access";
    public static final String RULE_MEMBER_ORDERING = "member-ordering";
    public static final String RULE_NO_ANY = "no-any";
    public static final String RULE_NO_ARG = "no-arg";
    public static final String RULE_NO_BITWISE = "no-bitwise";
    public static final String RULE_NO_CONDITIONAL_ASSIGNMENT = "no-conditional-assignment";
    public static final String RULE_NO_CONSECUTIVE_BLANK_LINES = "no-consecutive-blank-lines";
    public static final String RULE_NO_CONSOLE = "no-console";
    public static final String RULE_NO_CONSTRUCT = "no-construct";
    public static final String RULE_NO_CONSTRUCTOR_VARS = "no-constructor-vars";
    public static final String RULE_NO_DEBUGGER = "no-debugger";
    public static final String RULE_NO_DUPLICATE_KEY = "no-duplicate-key";
    public static final String RULE_NO_DUPLICATE_VARIABLE = "no-duplicate-variable";
    public static final String RULE_NO_EMPTY = "no-empty";
    public static final String RULE_NO_EVAL = "no-eval";
    public static final String RULE_NO_INFERRABLE_TYPES = "no-inferrable-types";
    public static final String RULE_NO_INTERNAL_MODULE = "no-internal-module";
    public static final String RULE_NO_NULL_KEYWORD = "no-null-keyword";
    public static final String RULE_NO_REQUIRE_IMPORTS = "no-require-imports";
    public static final String RULE_NO_SHADOWED_VARIABLE = "no-shadowed-variable";
    public static final String RULE_NO_STRING_LITERAL = "no-string-literal";
    public static final String RULE_NO_SWITCH_CASE_FALL_THROUGH = "no-switch-case-fall-through";
    public static final String RULE_NO_TRAILING_WHITESPACE = "no-trailing-whitespace";
    public static final String RULE_NO_UNREACHABLE = "no-unreachable";
    public static final String RULE_NO_UNUSED_EXPRESSION = "no-unused-expression";
    public static final String RULE_NO_UNUSED_VARIABLE = "no-unused-variable";
    public static final String RULE_NO_USE_BEFORE_DECLARE = "no-use-before-declare";
    public static final String RULE_NO_VAR_KEYWORD = "no-var-keyword";
    public static final String RULE_NO_VAR_REQUIRES = "no-var-requires";
    public static final String RULE_OBJECT_LITERAL_SORT_KEYS = "object-literal-sort-keys";
    public static final String RULE_ONE_LINE = "one-line";
    public static final String RULE_QUOTEMARK = "quotemark";
    public static final String RULE_RADIX = "radix";
    public static final String RULE_SEMICOLON = "semicolon";
    public static final String RULE_SWITCH_DEFAULT = "switch-default";
    public static final String RULE_TRAILING_COMMA = "trailing-comma";
    public static final String RULE_TRIPLE_EQUALS = "triple-equals";
    public static final String RULE_TYPEDEF = "typedef";
    public static final String RULE_TYPEDEF_WHITESPACE = "typedef-whitespace";
    public static final String RULE_USE_STRICT = "use-strict";
    public static final String RULE_VARIABLE_NAME = "variable-name";
    public static final String RULE_WHITESPACE = "whitespace";

    public void define(Context context) {
        NewRepository repository =
                context
                .createRepository(REPOSITORY_NAME, TypeScriptLanguage.LANGUAGE_KEY)
                .setName("TsLint Analyser");

        repository.createRule(RULE_TSLINT_ISSUE).setName("tslint issues that are not yet known to the plugin").setSeverity(Severity.MAJOR);

        repository.createRule(RULE_ALIGN).setName("enforces vertical alignment of parameters, arguments and statements").setSeverity(Severity.MINOR);
        repository.createRule(RULE_BAN).setName("Use of this method is banned by current configuration").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_CLASS_NAME).setName("Name must use PascalCase").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_COMMENT_FORMAT).setName("Comments must be correctly formatted").setSeverity(Severity.MINOR);
        repository.createRule(RULE_CURLY).setName("enforces braces for if/for/do/while statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_EOFLINE).setName("enforces the file to end with a newline").setSeverity(Severity.MINOR);
        repository.createRule(RULE_FORIN).setName("enforces a for ... in statement to be filtered with an if statement").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_INDENT).setName("enforces consistent indentation with tabs or spaces").setSeverity(Severity.MINOR);
        repository.createRule(RULE_INTERFACE_NAME).setName("enforces the rule that interface names must begin with a capital I").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_JSDOC_FORMAT).setName("enforces basic format rules for jsdoc comments - comments starting with /**").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_LABEL_POSITION).setName("enforces labels only on sensible statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_LABEL_UNDEFINED).setName("checks that labels are defined before usage").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_MAX_LINE_LENGTH).setName("sets the maximum length of a line").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_MEMBER_ACCESS).setName("enforces using explicit visibility on class members").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_MEMBER_ORDERING).setName("enforces ordering of class members").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_ANY).setName("'any' must not be used as a type decoration").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_ARG).setName("arguments.callee must not be used").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_BITWISE).setName("bitwise operators must not be used").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_CONDITIONAL_ASSIGNMENT).setName("disallows any type of assignment in conditionals - this applies to do-while, for, if and while statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_CONSECUTIVE_BLANK_LINES).setName("No more than one blank line should appear in a row").setSeverity(Severity.MINOR);
        repository.createRule(RULE_NO_CONSOLE).setName("Specified function must not be called on the global console object").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_CONSTRUCT).setName("Constructors of String, Number and Boolean must not be used").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_CONSTRUCTOR_VARS).setName("Public and private modifiers must not be used on constructor arguments").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_DEBUGGER).setName("Debugger statements are not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_DUPLICATE_KEY).setName("Duplicate keys must not be specified in object literals").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_DUPLICATE_VARIABLE).setName("Duplicate variable definitions are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_EMPTY).setName("Empty blocks are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_EVAL).setName("Use of eval is not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_INFERRABLE_TYPES).setName("disallows explicit type declarations for variables or parameters initialised to a number, string or boolean").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_INTERNAL_MODULE).setName("disallows internal modules - use namespaces instead").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_NULL_KEYWORD).setName("disallows use of the null keyword literal").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_REQUIRE_IMPORTS).setName("disallows invocation of require() - use ES6-style imports instead").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_SHADOWED_VARIABLE).setName("disallows shadowed variable declarations").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_STRING_LITERAL).setName("Object access via string literals is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_SWITCH_CASE_FALL_THROUGH).setName("Falling through one case statement to another is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_TRAILING_WHITESPACE).setName("Trailing whitespace at the end of lines is not allowed").setSeverity(Severity.MINOR);
        repository.createRule(RULE_NO_UNREACHABLE).setName("Unreachable code after break, catch, throw and return statements is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_UNUSED_EXPRESSION).setName("Unused expressions (those that aren't assignments or function calls) are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_UNUSED_VARIABLE).setName("Unused imports, variables, functions and private class members are not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_USE_BEFORE_DECLARE).setName("Variable use before declaration is not allowed").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_NO_VAR_KEYWORD).setName("disallows usage of the var keyword - use let or const instead").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_NO_VAR_REQUIRES).setName("Require is only allowed in import statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_OBJECT_LITERAL_SORT_KEYS).setName("checks that keys in object literals are declared in alphabetical order (useful to prevent merge conflicts)").setSeverity(Severity.MINOR);
        repository.createRule(RULE_ONE_LINE).setName("No newline is allowed before keyword").setSeverity(Severity.MINOR);
        repository.createRule(RULE_QUOTEMARK).setName("Consistent use of single or double quotes is required - a mixture is not allowed").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_RADIX).setName("A radix must be specified when calling parseInt").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_SEMICOLON).setName("Statement must end with a semicolon").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_SWITCH_DEFAULT).setName("enforces a default case in switch statements").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_TRAILING_COMMA).setName("enforces a standard for trailing commas within array and object literals, destructuring assignment and named imports").setSeverity(Severity.MINOR);
        repository.createRule(RULE_TRIPLE_EQUALS).setName("== and != must not be used - use === or !== instead").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_TYPEDEF).setName("Type definition must be specified").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_TYPEDEF_WHITESPACE).setName("Whitespace around type definitions must be correct").setSeverity(Severity.MINOR);
        repository.createRule(RULE_USE_STRICT).setName("Strict mode must be used").setSeverity(Severity.CRITICAL);
        repository.createRule(RULE_VARIABLE_NAME).setName("Variable names must be either camelCased or UPPER_CASED").setSeverity(Severity.MAJOR);
        repository.createRule(RULE_WHITESPACE).setName("Inappropriate whitespace between tokens").setSeverity(Severity.MINOR);

        for (NewRule rule : repository.rules()) {
            rule.setHtmlDescription("HTML description to follow");
            rule.setStatus(RuleStatus.READY);
        }

        repository.done();
    }
}
