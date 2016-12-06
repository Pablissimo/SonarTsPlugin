package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;

import java.util.*;

public class TsLintSensor implements Sensor {
    public static final String CONFIG_FILENAME = "tslint.json";
    public static final String TSLINT_FALLBACK_PATH = "node_modules/tslint/bin/tslint";

    private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);

    private Settings settings;
    private System2 system;
    private TempFolder tempFolder;
    
    public TsLintSensor(Settings settings, System2 system, TempFolder tempFolder) {
        this.settings = settings;
        this.system = system;
        this.tempFolder = tempFolder;
    }
    
    protected PathResolver getPathResolver() {
        return new PathResolverImpl();
    }

    protected TsLintExecutor getTsLintExecutor() {
        return new TsLintExecutorImpl(this.system, this.tempFolder);
    }

    protected TsLintParser getTsLintParser() {
        return new TsLintParserImpl();
    }

    protected TsRulesDefinition getTsRulesDefinition() {
        return new TsRulesDefinition(this.settings);
    }

    @Override
    public void describe(SensorDescriptor desc) {
        desc
            .name("Linting sensor for TypeScript files")
            .onlyOnLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext ctx) {
        PathResolver resolver = getPathResolver();
        
        String pathToTsLint = resolver.getPath(ctx, TypeScriptPlugin.SETTING_TS_LINT_PATH, TSLINT_FALLBACK_PATH);
        String pathToTsLintConfig = resolver.getPath(ctx, TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH, CONFIG_FILENAME);
        String rulesDir = resolver.getPath(ctx, TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR, null);
        
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

        Collection<ActiveRule> allRules = ctx.activeRules().findByRepository(TsRulesDefinition.REPOSITORY_NAME);
        HashSet<String> ruleNames = new HashSet<>();
        for (ActiveRule rule : allRules) {
            ruleNames.add(rule.ruleKey().rule());
        }

        List<String> paths = new ArrayList<String>();
        HashMap<String, InputFile> fileMap = new HashMap<String, InputFile>();

        for (InputFile file : ctx.fileSystem().inputFiles(ctx.fileSystem().predicates().hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            if (skipTypeDefFiles && file.file().getName().toLowerCase().endsWith("." + TypeScriptLanguage.LANGUAGE_DEFINITION_EXTENSION)) {
                continue;
            }

            String pathAdjusted = file.absolutePath().replace('\\', '/');
            paths.add(pathAdjusted);
            fileMap.put(pathAdjusted, file);
        }

        List<String> jsonResults = executor.execute(pathToTsLint, pathToTsLintConfig, rulesDir, paths, tsLintTimeoutMs);

        Map<String, List<TsLintIssue>> issues = parser.parse(jsonResults);

        if (issues == null) {
            LOG.warn("TsLint returned no result at all");
            return;
        }

        // Each issue bucket will contain info about a single file
        for (String filePath : issues.keySet()) {
            List<TsLintIssue> batchIssues = issues.get(filePath);
            
            if (batchIssues == null || batchIssues.size() == 0) {
                continue;
            }
            
            if (!fileMap.containsKey(filePath)) {
                LOG.warn("TsLint reported issues against a file that wasn't sent to it - will be ignored: " + filePath);
                continue;
            }

            InputFile file = fileMap.get(filePath);
            
            for (TsLintIssue issue : batchIssues) {
                // Make sure the rule we're violating is one we recognise - if not, we'll
                // fall back to the generic 'tslint-issue' rule
                String ruleName = issue.getRuleName();
                if (!ruleNames.contains(ruleName)) {
                    ruleName = TsRulesDefinition.TSLINT_UNKNOWN_RULE.key;
                }

                NewIssue newIssue = 
                        ctx
                        .newIssue()
                        .forRule(RuleKey.of(TsRulesDefinition.REPOSITORY_NAME, ruleName));
                
                NewIssueLocation newIssueLocation = 
                        newIssue
                        .newLocation()
                        .on(file)
                        .message(issue.getFailure())
                        .at(file.selectLine(issue.getStartPosition().getLine() + 1))
                        /*.at(
                                file
                                .newRange(
                                        issue.getStartPosition().getLine(), 
                                        issue.getStartPosition().getCharacter(), 
                                        issue.getEndPosition().getLine(), 
                                        issue.getEndPosition().getCharacter()
                                )
                         )*/;
                
                newIssue.at(newIssueLocation);
                newIssue.save();
            }
        }
    }
}
