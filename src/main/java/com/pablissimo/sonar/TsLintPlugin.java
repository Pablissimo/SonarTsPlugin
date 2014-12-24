package com.pablissimo.sonar;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

@Properties({
	@Property(
		key = "sonar.ts.tslintpath",
		defaultValue = "",
		name = "Path to TSLint",
		description = "Path to installed Node TSLint",
		project = false, 
		global = true
	),
	@Property(
		key = "sonar.ts.excludetypedefinitionfiles",
		type = PropertyType.BOOLEAN,
		defaultValue = "true",
		name = "Exclude .d.ts files",
		description = "Exclude .d.ts files from analysis",
		project = true,
		global = false
	)
	})
public class TsLintPlugin extends SonarPlugin {
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
