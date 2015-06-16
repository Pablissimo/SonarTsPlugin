package com.pablissimo.sonar;

import java.util.Arrays;
import java.util.List;

import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarPlugin;

@Properties({
	@Property(
		key = TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES,
		type = PropertyType.BOOLEAN,
		defaultValue = "true",
		name = "Exclude .d.ts files",
		description = "Exclude .d.ts files from analysis",
		project = true,
		global = false
	),
	@Property(
		key = TypeScriptPlugin.SETTING_LCOV_REPORT_PATH,
		type = PropertyType.STRING,
		name = "LCOV report path",
		description = "LCOV report path",
		project = true,
		global = false
	),
	@Property(
			key = TypeScriptPlugin.SETTING_CUSTOM_RULES_DIRECTORY,
			defaultValue = "",
			name = "Custom Rules Directory",
			description = "Path to Custom Rules Directory",
			project = false,
			global = true
	)
	})
public class TypeScriptPlugin extends SonarPlugin {
	public static final String SETTING_EXCLUDE_TYPE_DEFINITION_FILES = "sonar.ts.excludetypedefinitionfiles";
	public static final String SETTING_FORCE_ZERO_COVERAGE = "sonar.ts.forceZeroCoverage";
	public static final String SETTING_LCOV_REPORT_PATH = "sonar.ts.lcov.reportpath";
	public static final String SETTING_CUSTOM_RULES_DIRECTORY = "sonar.ts.custom.rule.dir";
	
	public List getExtensions() {
		return Arrays.asList
				(
					TypeScriptRuleProfile.class,
					TypeScriptLanguage.class, 
				    TsLintSensor.class,
				    TsCoverageSensor.class,
					TsRulesDefinition.class
				);
	}
}
