package com.pablissimo.sonar;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfile extends ProfileDefinition {		
	@Override
	public RulesProfile createProfile(ValidationMessages validation) {
		RulesProfile profile = RulesProfile.create("TsLint",  "ts");

		activateRule(profile, "no-any");
		activateRule(profile, "triple-equals");
		activateRule(profile, "radix");
		activateRule(profile, "no-arg");
		activateRule(profile, "no-bitwise");
		activateRule(profile, "label-undefined");
		activateRule(profile, "comment-format");
		activateRule(profile, "quotemark");
		activateRule(profile, "no-construct");
		activateRule(profile, "no-debuger");
		activateRule(profile, "no-duplicate-key");
		activateRule(profile, "no-duplicate-variable");
		activateRule(profile, "no-empty");
		activateRule(profile, "forin");
		activateRule(profile, "curly");
		activateRule(profile, "jsdoc-format");
		activateRule(profile, "indent");
		activateRule(profile, "label-position");
		activateRule(profile, "member-ordering");
		activateRule(profile, "eofline");
		activateRule(profile, "interface-name");
		activateRule(profile, "no-switch-case-fall-through");
		activateRule(profile, "whitespace");
		activateRule(profile, "class-name");
		activateRule(profile, "no-consecutive-blank-links");
		activateRule(profile, "one-line");
		activateRule(profile, "no-string-literal");
		activateRule(profile, "no-constructor-vars");
		activateRule(profile, "no-var-requires");
		activateRule(profile, "max-line-length");
		activateRule(profile, "no-console");
		activateRule(profile, "semicolon");
		activateRule(profile, "use-strict");
		activateRule(profile, "no-trailing-comma");
		activateRule(profile, "no-trailing-whitespace");
		activateRule(profile, "typedef");
		activateRule(profile, "no-unreachable");
		activateRule(profile, "no-unused-expression");
		activateRule(profile, "no-unused-variable");
		activateRule(profile, "no-eval");
		activateRule(profile, "ban");
		activateRule(profile, "variable-name");
		activateRule(profile, "no-use-before-declare");
		activateRule(profile, "typedef-whitespace");
		
		return profile;
	}
	
	private static void activateRule(RulesProfile profile, String ruleKey) {
	    profile.activateRule(Rule.create("tslint", ruleKey), null);
	}
}
