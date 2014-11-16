package com.pablissimo.sonar;

import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfile extends ProfileDefinition {		
	@Override
	public RulesProfile createProfile(ValidationMessages validation) {
		RulesProfile profile = RulesProfile.create("TsLint",  "ts");
		
		return profile;
	}
}
