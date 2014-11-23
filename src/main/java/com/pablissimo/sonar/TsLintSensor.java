package com.pablissimo.sonar;

import java.io.File;

import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.issue.Issuable;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;

import com.pablissimo.sonar.model.TsLintIssue;

public class TsLintSensor implements Sensor {
  	private FileSystem fileSystem;
  	private FilePredicates filePredicates;
  	private ResourcePerspectives perspectives;
  
	public TsLintSensor(FileSystem fileSystem, ResourcePerspectives perspectives) {
		this.fileSystem = fileSystem;
		this.filePredicates = fileSystem.predicates();
		this.perspectives = perspectives;
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
		
		for (File file : fileSystem.files(this.filePredicates.hasLanguage("ts"))) {
			Resource resource = org.sonar.api.resources.File.fromIOFile(file, project);
			Issuable issuable = perspectives.as(Issuable.class, resource);
			String jsonResult = executor.execute("C:\\Users\\Pabliissimo\\AppData\\Roaming\\npm\\node_modules\\tslint\\bin\\tslint", "C:\\temp\\tslint.json", file.getAbsolutePath());
			
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
}
