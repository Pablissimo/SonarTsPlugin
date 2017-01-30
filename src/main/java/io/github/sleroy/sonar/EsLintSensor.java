package io.github.sleroy.sonar;

import io.github.sleroy.sonar.model.EsLintIssue;
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

import java.util.*;

public class EsLintSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(EsLintExecutorImpl.class);

    private Settings settings;
    private PathResolver resolver;
    private EsLintExecutor executor;
    private EsLintParser parser;
    
    public EsLintSensor(Settings settings, PathResolver resolver,
                        EsLintExecutor executor, EsLintParser parser) {
        this.settings = settings;
        this.resolver = resolver;
        this.executor = executor;
        this.parser = parser;
    }
    
    @Override
    public void describe(SensorDescriptor desc) {
        desc
            .name("Linting sensor for TypeScript files")
            .onlyOnLanguage(EsLintLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext ctx) {    
        if (!this.settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)) {
            LOG.debug("Skipping tslint execution - " + EsLintPlugin.SETTING_ES_LINT_ENABLED + " set to false");
            return;
        }
        
        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(this.settings, ctx, this.resolver);
        
        if (config.getPathToTsLint() == null) {
            LOG.warn("Path to tslint not defined or not found. Skipping tslint analysis.");
            return;
        }
        else if (config.getConfigFile() == null && config.getPathToTsConfig() == null) {
            LOG.warn("Path to tslint.json and tsconfig.json configuration files either not defined or not found - at least one is required. Skipping tslint analysis.");
            return;
        }


        Collection<ActiveRule> allRules = ctx.activeRules().findByRepository(EsRulesDefinition.REPOSITORY_NAME);
        HashSet<String> ruleNames = new HashSet<>();
        for (ActiveRule rule : allRules) {
            ruleNames.add(rule.ruleKey().rule());
        }

        List<String> paths = new ArrayList<String>();
        HashMap<String, InputFile> fileMap = new HashMap<String, InputFile>();

        for (InputFile file : ctx.fileSystem().inputFiles(ctx.fileSystem().predicates().hasLanguage(EsLintLanguage.LANGUAGE_KEY))) {

            String pathAdjusted = file.absolutePath().replace('\\', '/');
            paths.add(pathAdjusted);
            fileMap.put(pathAdjusted, file);
        }
        
        List<String> jsonResults = this.executor.execute(config, paths);

        Map<String, List<EsLintIssue>> issues = this.parser.parse(jsonResults);

        if (issues == null) {
            LOG.warn("TsLint returned no result at all");
            return;
        }

        // Each issue bucket will contain info about a single file
        for (String filePath : issues.keySet()) {
            List<EsLintIssue> batchIssues = issues.get(filePath);
            
            if (batchIssues == null || batchIssues.size() == 0) {
                continue;
            }

            if (!fileMap.containsKey(filePath)) {
                LOG.warn("TsLint reported issues against a file that wasn't sent to it - will be ignored: " + filePath);
                continue;
            }

            InputFile file = fileMap.get(filePath);
            
            for (EsLintIssue issue : batchIssues) {
                // Make sure the rule we're violating is one we recognise - if not, we'll
                // fall back to the generic 'tslint-issue' rule
                String ruleName = issue.getRuleName();
                if (!ruleNames.contains(ruleName)) {
                    ruleName = EsRulesDefinition.ESLINT_UNKNOWN_RULE.key;
                }

                NewIssue newIssue = 
                        ctx
                        .newIssue()
                        .forRule(RuleKey.of(EsRulesDefinition.REPOSITORY_NAME, ruleName));
                
                NewIssueLocation newIssueLocation = 
                        newIssue
                        .newLocation()
                        .on(file)
                        .message(issue.getFailure())
                        .at(file.selectLine(issue.getStartPosition().getLine() + 1));
                
                newIssue.at(newIssueLocation);
                newIssue.save();
            }
        }
    }
}
