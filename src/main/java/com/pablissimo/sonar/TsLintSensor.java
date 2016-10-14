package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.issue.Issuable;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleFinder;
import org.sonar.api.rules.RuleQuery;
import org.sonar.api.utils.System2;

import java.io.File;
import java.util.*;

public class TsLintSensor implements Sensor {
    public static final String CONFIG_FILENAME = "tslint.json";
    public static final String TSLINT_FALLBACK_PATH = "node_modules/tslint/bin/tslint";

    private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);

    private Settings settings;
    private FileSystem fileSystem;
    private FilePredicates filePredicates;
    private ResourcePerspectives perspectives;
    private RuleFinder ruleFinder;
    private System2 system;
    
    public TsLintSensor(Settings settings, FileSystem fileSystem, ResourcePerspectives perspectives, RuleFinder ruleFinder, System2 system) {
        this.settings = settings;
        this.fileSystem = fileSystem;
        this.filePredicates = fileSystem.predicates();
        this.perspectives = perspectives;
        this.ruleFinder = ruleFinder;
        this.system = system;
    }

    public boolean shouldExecuteOnProject(Project project) {
        return hasFilesToAnalyze();
    }

    private boolean hasFilesToAnalyze() {
        return fileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY)).iterator().hasNext();
    }
    
    private String getPath(String settingKey, String defaultValue) {
        // Prefer the specified path
        String toReturn = settings.getString(settingKey);

        // Fall back to a file system search if null or doesn't exist
        if (toReturn == null || toReturn.isEmpty()) {
            LOG.debug("Path " + settingKey + " not specified, falling back to " + defaultValue);
            toReturn = defaultValue;
        }
        else {
            LOG.debug("Found " + settingKey + " Lint path to be '" + toReturn + "'");
        }
        
        return getAbsolutePath(toReturn);
    }
    
    protected String getAbsolutePath(String toReturn) {
        if (toReturn != null) {
            File candidateFile = new java.io.File(toReturn);
            if (!candidateFile.isAbsolute()) {
                candidateFile = new java.io.File(this.fileSystem.baseDir().getAbsolutePath(), toReturn);
            }
            
            if (!doesFileExist(candidateFile)) {
                return null;
            }

            return candidateFile.getAbsolutePath();
        }
        
        return null;
    }
    
    protected boolean doesFileExist(File f) {
        return f.exists();
    }

    public void analyse(Project project, SensorContext context) {
        String pathToTsLint = this.getPath(TypeScriptPlugin.SETTING_TS_LINT_PATH, TSLINT_FALLBACK_PATH);
        String pathToTsLintConfig = this.getPath(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH, CONFIG_FILENAME);
        String rulesDir = this.getPath(TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR, null);
        
        Integer tsLintTimeoutMs = Math.max(5000, settings.getInt(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT));

        if (pathToTsLint == null) {
            LOG.warn("Path to tslint not defined or not found. Skipping tslint analysis.");
            return;
        }
        else if (pathToTsLintConfig == null) {
            LOG.warn("Path to tslint.json configuration file not defined or not found. Skipping tslint analysis.");
            return;
        }

        TsLintExecutor executor = this.getTsLintExecutor();
        TsLintParser parser = this.getTsLintParser();

        boolean skipTypeDefFiles = settings.getBoolean(TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES);

        RuleQuery ruleQuery = RuleQuery.create().withRepositoryKey(TsRulesDefinition.REPOSITORY_NAME);
        Collection<Rule> allRules = this.ruleFinder.findAll(ruleQuery);
        HashSet<String> ruleNames = new HashSet<>();
        for (Rule rule : allRules) {
            ruleNames.add(rule.getKey());
        }

        List<String> paths = new ArrayList<String>();
        HashMap<String, File> fileMap = new HashMap<String, File>();

        for (File file : fileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            if (skipTypeDefFiles && file.getName().toLowerCase().endsWith("." + TypeScriptLanguage.LANGUAGE_DEFINITION_EXTENSION)) {
                continue;
            }

            String pathAdjusted = file.getAbsolutePath().replace('\\', '/');
            paths.add(pathAdjusted);
            fileMap.put(pathAdjusted, file);
        }

        String jsonResult = executor.execute(pathToTsLint, pathToTsLintConfig, rulesDir, paths, tsLintTimeoutMs);

        TsLintIssue[][] issues = parser.parse(jsonResult);

        if (issues == null) {
            LOG.warn("TsLint returned no result at all");
            return;
        }

        // Each issue bucket will contain info about a single file
        for (TsLintIssue[] batchIssues : issues) {
            if (batchIssues == null || batchIssues.length == 0) {
                continue;
            }

            String filePath = batchIssues[0].getName();
            
            if (!fileMap.containsKey(filePath)) {
                LOG.warn("TsLint reported issues against a file that wasn't sent to it - will be ignored: " + filePath);
                continue;
            }

            File file = fileMap.get(filePath);
            Resource resource = this.getFileFromIOFile(file, project);
            Issuable issuable = perspectives.as(Issuable.class, resource);

            for (TsLintIssue issue : batchIssues) {
                // Make sure the rule we're violating is one we recognise - if not, we'll
                // fall back to the generic 'tslint-issue' rule
                String ruleName = issue.getRuleName();
                if (!ruleNames.contains(ruleName)) {
                    ruleName = TsRulesDefinition.TSLINT_UNKNOWN_RULE.key;
                }

                issuable.addIssue
                (
                        issuable
                        .newIssueBuilder()
                        .line(issue.getStartPosition().getLine() + 1)
                        .message(issue.getFailure())
                        .ruleKey(RuleKey.of(TsRulesDefinition.REPOSITORY_NAME, ruleName))
                        .build()
                        );
            }
        }
    }

    protected org.sonar.api.resources.File getFileFromIOFile(File file, Project project) {
        return org.sonar.api.resources.File.fromIOFile(file, project);
    }

    protected TsLintExecutor getTsLintExecutor() {
        return new TsLintExecutorImpl(this.system);
    }

    protected TsLintParser getTsLintParser() {
        return new TsLintParserImpl();
    }

    protected TsRulesDefinition getTsRulesDefinition() {
        return new TsRulesDefinition(this.settings);
    }
}
