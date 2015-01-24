package com.pablissimo.sonar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.ActiveRule;
import org.sonar.api.rules.ActiveRuleParam;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.pablissimo.sonar.model.TsLintConfig;
import com.pablissimo.sonar.model.TsLintIssue;

public class TsLintSensor implements Sensor {
  	private Settings settings;
	private FileSystem fileSystem;
  	private FilePredicates filePredicates;
  	private ResourcePerspectives perspectives;
  	private RulesProfile rulesProfile;
  
	public TsLintSensor(Settings settings, FileSystem fileSystem, ResourcePerspectives perspectives, RulesProfile rulesProfile) {
		this.settings = settings;
		this.fileSystem = fileSystem;
		this.filePredicates = fileSystem.predicates();
		this.perspectives = perspectives;
		this.rulesProfile = rulesProfile;
	}
	
	public boolean shouldExecuteOnProject(Project project) {
		boolean toReturn = hasFilesToAnalyze();
		
		return toReturn;
	}
	
	private boolean hasFilesToAnalyze() {
		return fileSystem.files(this.filePredicates.hasLanguage("ts")).iterator().hasNext();
	}

	public void analyse(Project project, SensorContext context) {
		TsLintExecutor executor = new TsLintExecutor();
		TsLintParser parser = new TsLintParser();
		
		// Build the config file
		File configFile = new File(this.fileSystem.workDir(), "tslint.json");
		TsLintConfig config = getConfiguration();
		String configSerialised = new GsonBuilder().setPrettyPrinting().create().toJson(config);

		try {
			Files.write(configSerialised, configFile, Charsets.UTF_8);
	    } catch (IOException e) {
	    	throw Throwables.propagate(e);
	    }
		
		boolean skipTypeDefFiles = settings.getBoolean("sonar.ts.excludetypedefinitionfiles");
		
		for (File file : fileSystem.files(this.filePredicates.hasLanguage("ts"))) {
			if (skipTypeDefFiles && file.getName().toLowerCase().endsWith(".d.ts")) {
				continue;
			}
			
			Resource resource = org.sonar.api.resources.File.fromIOFile(file, project);
			Issuable issuable = perspectives.as(Issuable.class, resource);
			
			String pathToTsLint = settings.getString("sonar.ts.tslintpath");
			String jsonResult = executor.execute(pathToTsLint, configFile.getPath(), file.getAbsolutePath());
			
			TsLintIssue[] issues = parser.parse(jsonResult);
			
			if (issues != null) {
				for (TsLintIssue issue : issues) {
					issuable.addIssue
					(
						issuable
							.newIssueBuilder()
							.line(issue.getStartPosition().getLine() + 1)
							.message(issue.getFailure())
							.ruleKey(RuleKey.of("tslint", issue.getRuleName()))
							.build()
					);
				}
			}
		}
	}
	
	private TsLintConfig getConfiguration() {
		TsLintConfig toReturn = new TsLintConfig();
		
		for (ActiveRule rule : this.rulesProfile.getActiveRulesByRepository("tslint")) {
			List<ActiveRuleParam> params = rule.getActiveRuleParams();
			
			if (params == null || params.size() == 0) {
				// Simple binary rule
				toReturn.addRule(rule.getRuleKey(), rule.isEnabled());
			}
			else {
				List<Object> processedParams = new ArrayList<Object>(params.size());
				processedParams.add(true);
				
				for (ActiveRuleParam param : params) {
					switch (param.getRuleParam().getType()) {
						case "BOOLEAN":
							if (param.getValue() == "true") {
								processedParams.add(param.getParamKey());
							}
							break;
						case "INTEGER":
							processedParams.add(Integer.parseInt(param.getValue()));
							break;
						default:
							processedParams.add(param.getValue());
							break;
					}
				}
				
				toReturn.addRule(rule.getRuleKey(), processedParams.toArray());
			}
		}
		
		return toReturn;
	}
}
