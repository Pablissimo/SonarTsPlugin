package io.github.sleroy.sonar;

import io.github.sleroy.sonar.model.EsLintRule;
import org.sonar.api.profiles.ProfileDefinition;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.rules.Rule;
import org.sonar.api.utils.ValidationMessages;

/**
 * This class defines the iinformations required to create a default rule profile for EsLint.
 */
public class EsLintRuleProfile extends ProfileDefinition {
    public static final String PROFILE_NAME = "eslint";

    private static void activateRule(RulesProfile profile, String ruleKey) {
        profile.activateRule(Rule.create(EsRulesDefinition.REPOSITORY_NAME, ruleKey), null);
    }

    @Override
    public RulesProfile createProfile(ValidationMessages validation) {
        RulesProfile profile = RulesProfile.create("EsLint", EsLintLanguage.LANGUAGE_KEY);

        EsRulesDefinition rules = new EsRulesDefinition();

        EsLintRuleProfile.activateRule(profile, EsRulesDefinition.ESLINT_UNKNOWN_RULE.getKey());

        for (EsLintRule coreRule : rules.getCoreRules()) {
            EsLintRuleProfile.activateRule(profile, coreRule.getKey());
        }

        return profile;
    }
}
