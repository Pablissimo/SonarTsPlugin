package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.EsLintExecutor;
import io.github.sleroy.sonar.api.EsLintParser;
import io.github.sleroy.sonar.api.PathResolver;
import io.github.sleroy.sonar.model.EsLintIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssue;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.config.Settings;
import org.sonar.api.rule.RuleKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class EsLintSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(EsLintSensor.class);

    private final Settings settings;
    private final PathResolver resolver;
    private final EsLintExecutor executor;
    private final EsLintParser parser;

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
                .name("Linting sensor for Javascript files")
            .onlyOnLanguage(EsLintLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext ctx) {
        if (!settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)) {
            EsLintSensor.LOG.debug("Skipping eslint execution - {} set to false", EsLintPlugin.SETTING_ES_LINT_ENABLED);
            return;
        }

        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(settings, ctx, resolver);

        if (config.getPathToEsLint() == null) {
            EsLintSensor.LOG.warn("Path to eslint not defined or not found. Skipping eslint analysis.");
            return;
        }
        if (config.getConfigFile() == null) {
            EsLintSensor.LOG.warn("Path to .eslintrc.* configuration file either not defined or not found - Skipping eslint analysis.");
            return;


        }


        Collection<ActiveRule> allRules = ctx.activeRules().findByRepository(EsRulesDefinition.REPOSITORY_NAME);
        Set<String> ruleNames = new HashSet<>(100);
        ruleNames.addAll(allRules.stream().map(rule -> rule.ruleKey().rule()).collect(Collectors.toList()));

        List<String> paths = new ArrayList<>(100);

        Map<String, InputFile> fileMap = new HashMap<>(100);
        for (InputFile file : ctx.fileSystem().inputFiles(ctx.fileSystem().predicates().hasLanguage(EsLintLanguage.LANGUAGE_KEY))) {

            String pathAdjusted = file.absolutePath();
            paths.add(pathAdjusted);
            fileMap.put(pathAdjusted, file);
        }


        List<String> jsonResults = executor.execute(config, paths);

        Map<String, List<EsLintIssue>> issues = parser.parse(jsonResults);

        if (issues == null) {
            EsLintSensor.LOG.warn("Eslint returned no result at all");
            return;
        }

        // Each issue bucket will contain info about a single file
        for (Map.Entry<String, List<EsLintIssue>> filePathEntry : issues.entrySet()) {
            List<EsLintIssue> batchIssues = filePathEntry.getValue();

            if (batchIssues == null || batchIssues.isEmpty()) {
                continue;
            }

            String filePath = filePathEntry.getKey();
            if (!fileMap.containsKey(filePath)) {
                EsLintSensor.LOG.warn("EsLint reported issues against a file that wasn't sent to it - will be ignored: {}", filePath);
                continue;
            }

            InputFile file = fileMap.get(filePath);
            
            for (EsLintIssue issue : batchIssues) {
                // Make sure the rule we're violating is one we recognise - if not, we'll
                // fall back to the generic 'eslint-issue' rule
                String ruleName = issue.getRuleId().replace('/', '-');
                if (!ruleNames.contains(ruleName)) {
                    EsLintSensor.LOG.warn("Rule {} has not yet being defined into the EsLint plugin", ruleName);
                    ruleName = EsRulesDefinition.ESLINT_UNKNOWN_RULE.getKey();
                }

                NewIssue newIssue =
                        ctx
                        .newIssue()
                        .forRule(RuleKey.of(EsRulesDefinition.REPOSITORY_NAME, ruleName));

                NewIssueLocation newIssueLocation =
                        newIssue
                        .newLocation()
                        .on(file)
                                .message(issue.getMessage())
                                .at(file.selectLine(issue.getLine()));
                
                newIssue.at(newIssueLocation);
                newIssue.save();
            }
        }
    }
}
