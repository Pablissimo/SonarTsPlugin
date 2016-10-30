package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintRule;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

public class TypeScriptRuleProfile extends ProfileDefinition {
    public static final String PROFILE_NAME = "tslint";

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        RulesProfile profile = RulesProfile.create("TsLint", TypeScriptLanguage.LANGUAGE_KEY);

        TsRulesDefinition rules = new TsRulesDefinition();

        activateRule(profile, TsRulesDefinition.TSLINT_UNKNOWN_RULE.key);

        for (TsLintRule coreRule : rules.getCoreRules()) {
            activateRule(profile, coreRule.key);
        }

        return profile;
    }

    private static void activateRule(RulesProfile profile, String ruleKey) {
        profile.activateRule(Rule.create(TsRulesDefinition.REPOSITORY_NAME, ruleKey), null);
    }
}
