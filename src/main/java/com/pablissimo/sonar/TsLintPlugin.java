package com.pablissimo.sonar;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.SonarPlugin;

public class TsLintPlugin extends SonarPlugin {
	public List getExtensions() {
		return Arrays.asList
				(
					TypeScriptRuleProfile.class,
					TypeScriptLanguage.class, 
				    TsLintSensor.class, 
					TsRulesDefinition.class
				);
	}
}
