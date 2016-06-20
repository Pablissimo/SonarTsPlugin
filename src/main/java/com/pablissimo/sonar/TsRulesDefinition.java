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

        NewRule ruleAlign = repository.createRule(RULE_ALIGN);
        ruleAlign
            .setName("enforces vertical alignment of parameters, arguments and statements")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleAlign.debtRemediationFunctions().constantPerIssue("5min"));

        NewRule ruleBan = repository.createRule(RULE_BAN);
        ruleBan
            .setName("Use of this method is banned by current configuration")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleBan.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleClassName = repository.createRule(RULE_CLASS_NAME);
        ruleClassName
            .setName("Name must use PascalCase")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleClassName.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleCommentFormat = repository.createRule(RULE_COMMENT_FORMAT);
        ruleCommentFormat
            .setName("Comments must be correctly formatted")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleCommentFormat.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleCurly = repository.createRule(RULE_CURLY);
        ruleCurly
            .setName("enforces braces for if/for/do/while statements")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleCurly.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleEofline = repository.createRule(RULE_EOFLINE);
        ruleEofline
            .setName("enforces the file to end with a newline")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleEofline.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleForin = repository.createRule(RULE_FORIN);
        ruleForin
            .setName("enforces a for ... in statement to be filtered with an if statement")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleForin.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleIndent = repository.createRule(RULE_INDENT);
        ruleIndent
            .setName("enforces consistent indentation with tabs or spaces")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleIndent.debtRemediationFunctions().constantPerIssue("2min"));


        NewRule ruleInterfaceName = repository.createRule(RULE_INTERFACE_NAME);
        ruleInterfaceName
            .setName("enforces the rule that interface names must begin with a capital I")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleInterfaceName.debtRemediationFunctions().constantPerIssue("1min"));

        NewRule ruleJsdocFormat = repository.createRule(RULE_JSDOC_FORMAT);
        ruleJsdocFormat
            .setName("enforces basic format rules for jsdoc comments - comments starting with /**")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleJsdocFormat.debtRemediationFunctions().constantPerIssue("3min"));


        NewRule ruleLabelPosition = repository.createRule(RULE_LABEL_POSITION);
        ruleLabelPosition
            .setName("enforces labels only on sensible statements")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleLabelPosition.debtRemediationFunctions().constantPerIssue("20min"));


        NewRule ruleLabelUndefined = repository.createRule(RULE_LABEL_UNDEFINED);
        ruleLabelUndefined
            .setName("checks that labels are defined before usage")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleLabelUndefined.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleMaxLineLength = repository.createRule(RULE_MAX_LINE_LENGTH);
        ruleMaxLineLength
            .setName("sets the maximum length of a line")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleMaxLineLength.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleMemberAccess = repository.createRule(RULE_MEMBER_ACCESS);
        ruleMemberAccess
            .setName("enforces using explicit visibility on class members")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleMemberAccess.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleMemberOrdering = repository.createRule(RULE_MEMBER_ORDERING);
        ruleMemberOrdering
            .setName("enforces ordering of class members")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleMemberOrdering.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoAny = repository.createRule(RULE_NO_ANY);
        ruleNoAny
            .setName("'any' must not be used as a type decoration")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoAny.debtRemediationFunctions().constantPerIssue("20min"));

        NewRule ruleNoArg = repository.createRule(RULE_NO_ARG);
        ruleNoArg
            .setName("arguments.callee must not be used")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoArg.debtRemediationFunctions().constantPerIssue("30min"));


        NewRule ruleNoBitwise = repository.createRule(RULE_NO_BITWISE);
        ruleNoBitwise
            .setName("bitwise operators must not be used")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoBitwise.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoConditionalAssignment = repository.createRule(RULE_NO_CONDITIONAL_ASSIGNMENT);
        ruleNoConditionalAssignment
            .setName("disallows any type of assignment in conditionals - this applies to do-while, for, if and while statements")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoConditionalAssignment.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoConsecutiveBlankLines = repository.createRule(RULE_NO_CONSECUTIVE_BLANK_LINES);
        ruleNoConsecutiveBlankLines
            .setName("No more than one blank line should appear in a row")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleNoConsecutiveBlankLines.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoConsole = repository.createRule(RULE_NO_CONSOLE);
        ruleNoConsole
            .setName("Specified function must not be called on the global console object")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoConsole.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoConstruct = repository.createRule(RULE_NO_CONSTRUCT);
        ruleNoConstruct
            .setName("Constructors of String, Number and Boolean must not be used")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoConstruct.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoConstructorVars = repository.createRule(RULE_NO_CONSTRUCTOR_VARS);
        ruleNoConstructorVars
            .setName("Public and private modifiers must not be used on constructor arguments")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoConstructorVars.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoDebugger = repository.createRule(RULE_NO_DEBUGGER);
        ruleNoDebugger
            .setName("Debugger statements are not allowed")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleNoDebugger.debtRemediationFunctions().constantPerIssue("5min"));

        NewRule ruleNoDuplicateKey = repository.createRule(RULE_NO_DUPLICATE_KEY);
        ruleNoDuplicateKey
            .setName("Duplicate keys must not be specified in object literals")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoDuplicateKey.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoDuplicateVariable = repository.createRule(RULE_NO_DUPLICATE_VARIABLE);
        ruleNoDuplicateVariable
            .setName("Duplicate variable definitions are not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoDuplicateVariable.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoEmpty = repository.createRule(RULE_NO_EMPTY);
        ruleNoEmpty
            .setName("Empty blocks are not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoEmpty.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoEval = repository.createRule(RULE_NO_EVAL);
        ruleNoEval
            .setName("Use of eval is not allowed")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleNoEval.debtRemediationFunctions().constantPerIssue("30min"));


        NewRule ruleNoInferrableTypes = repository.createRule(RULE_NO_INFERRABLE_TYPES);
        ruleNoInferrableTypes
            .setName("disallows explicit type declarations for variables or parameters initialised to a number, string or boolean")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoInferrableTypes.debtRemediationFunctions().constantPerIssue("5min"));

        NewRule ruleNoInternalModule = repository.createRule(RULE_NO_INTERNAL_MODULE);
        ruleNoInternalModule
            .setName("disallows internal modules - use namespaces instead")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoInternalModule.debtRemediationFunctions().constantPerIssue("5min"));

        NewRule ruleNoNullKeyword = repository.createRule(RULE_NO_NULL_KEYWORD);
        ruleNoNullKeyword
            .setName("disallows use of the null keyword literal")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoNullKeyword.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoRequireImports = repository.createRule(RULE_NO_REQUIRE_IMPORTS);
        ruleNoRequireImports
            .setName("disallows invocation of require() - use ES6-style imports instead")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoRequireImports.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoShadowedVariable = repository.createRule(RULE_NO_SHADOWED_VARIABLE);
        ruleNoShadowedVariable
            .setName("disallows shadowed variable declarations")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoShadowedVariable.debtRemediationFunctions().constantPerIssue("10min"));


        NewRule ruleNoStringLiteral = repository.createRule(RULE_NO_STRING_LITERAL);
        ruleNoStringLiteral
            .setName("Object access via string literals is not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoStringLiteral.debtRemediationFunctions().constantPerIssue("10min"));


        NewRule ruleNoSwitchCaseFallThrough = repository.createRule(RULE_NO_SWITCH_CASE_FALL_THROUGH);
        ruleNoSwitchCaseFallThrough
            .setName("Falling through one case statement to another is not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoSwitchCaseFallThrough.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoTrailingWhitespace = repository.createRule(RULE_NO_TRAILING_WHITESPACE);
        ruleNoTrailingWhitespace
            .setName("Trailing whitespace at the end of lines is not allowed")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleNoTrailingWhitespace.debtRemediationFunctions().constantPerIssue("1min"));

        NewRule ruleNoUnreachable = repository.createRule(RULE_NO_UNREACHABLE);
        ruleNoUnreachable
            .setName("Unreachable code after break, catch, throw and return statements is not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoUnreachable.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleNoUnusedExpression = repository.createRule(RULE_NO_UNUSED_EXPRESSION);
        ruleNoUnusedExpression
            .setName("Unused expressions (those that aren't assignments or function calls) are not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoUnusedExpression.debtRemediationFunctions().constantPerIssue("1min"));

        NewRule ruleNoUnusedVariable = repository.createRule(RULE_NO_UNUSED_VARIABLE);
        ruleNoUnusedVariable
            .setName("Unused imports, variables, functions and private class members are not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoUnusedVariable.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleNoUseBeforeDeclare = repository.createRule(RULE_NO_USE_BEFORE_DECLARE);
        ruleNoUseBeforeDeclare
            .setName("Variable use before declaration is not allowed")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleNoUseBeforeDeclare.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleNoVarKeyword = repository.createRule(RULE_NO_VAR_KEYWORD);
        ruleNoVarKeyword
            .setName("disallows usage of the var keyword - use let or const instead")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoVarKeyword.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleNoVarRequires = repository.createRule(RULE_NO_VAR_REQUIRES);
        ruleNoVarRequires
            .setName("Require is only allowed in import statements")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleNoVarRequires.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleObjectLiteralSortKeys = repository.createRule(RULE_OBJECT_LITERAL_SORT_KEYS);
        ruleObjectLiteralSortKeys
            .setName("checks that keys in object literals are declared in alphabetical order (useful to prevent merge conflicts)")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleObjectLiteralSortKeys.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleOneLine = repository.createRule(RULE_ONE_LINE);
        ruleOneLine
            .setName("No newline is allowed before keyword")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleOneLine.debtRemediationFunctions().constantPerIssue("1min"));

        NewRule ruleQuotemark = repository.createRule(RULE_QUOTEMARK);
        ruleQuotemark
            .setName("Consistent use of single or double quotes is required - a mixture is not allowed")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleQuotemark.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleRadix = repository.createRule(RULE_RADIX);
        ruleRadix
            .setName("A radix must be specified when calling parseInt")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleQuotemark.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleSemicolon = repository.createRule(RULE_SEMICOLON);
        ruleSemicolon
            .setName("Statement must end with a semicolon")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleSemicolon.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleSwitchDefault = repository.createRule(RULE_SWITCH_DEFAULT);
        ruleSwitchDefault
            .setName("enforces a default case in switch statements")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleSwitchDefault.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleTrailingComma = repository.createRule(RULE_TRAILING_COMMA);
        ruleTrailingComma
            .setName("enforces a standard for trailing commas within array and object literals, destructuring assignment and named imports")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleTrailingComma.debtRemediationFunctions().constantPerIssue("1min"));


        NewRule ruleTripleEquals = repository.createRule(RULE_TRIPLE_EQUALS);
        ruleTripleEquals
            .setName("== and != must not be used - use === or !== instead")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleTripleEquals.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleTypedef = repository.createRule(RULE_TYPEDEF);
        ruleTypedef
            .setName("Type definition must be specified")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleTypedef.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleTypedefWhitespace = repository.createRule(RULE_TYPEDEF_WHITESPACE);
        ruleTypedefWhitespace
            .setName("Whitespace around type definitions must be correct")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleTypedefWhitespace.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleUseStrict = repository.createRule(RULE_USE_STRICT);
        ruleUseStrict
            .setName("Strict mode must be used")
            .setSeverity(Severity.CRITICAL)
            .setDebtRemediationFunction(ruleUseStrict.debtRemediationFunctions().constantPerIssue("5min"));

        NewRule ruleVariableName = repository.createRule(RULE_VARIABLE_NAME);
        ruleVariableName
            .setName("Variable names must be either camelCased or UPPER_CASED")
            .setSeverity(Severity.MAJOR)
            .setDebtRemediationFunction(ruleUseStrict.debtRemediationFunctions().constantPerIssue("5min"));


        NewRule ruleWhitespace = repository.createRule(RULE_WHITESPACE);
        ruleWhitespace
            .setName("Inappropriate whitespace between tokens")
            .setSeverity(Severity.MINOR)
            .setDebtRemediationFunction(ruleWhitespace.debtRemediationFunctions().constantPerIssue("5min"));

        for (NewRule rule : repository.rules()) {
            rule.setHtmlDescription("HTML description to follow");
            rule.setStatus(RuleStatus.READY);
        }

        repository.done();
    }
}
