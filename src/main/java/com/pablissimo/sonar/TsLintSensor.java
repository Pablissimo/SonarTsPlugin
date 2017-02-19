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

import java.io.File;
import java.util.*;

public class TsLintSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(TsLintExecutorImpl.class);

    private Settings settings;
    private PathResolver resolver;
    private TsLintExecutor executor;
    private TsLintParser parser;

    public TsLintSensor(Settings settings, PathResolver resolver, TsLintExecutor executor, TsLintParser parser) {
        this.settings = settings;
        this.resolver = resolver;
        this.executor = executor;
        this.parser = parser;
    }

    @Override
    public void describe(SensorDescriptor desc) {
        desc
            .name("Linting sensor for TypeScript files")
            .onlyOnLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext ctx) {
        if (!this.settings.getBoolean(TypeScriptPlugin.SETTING_TS_LINT_ENABLED)) {
            LOG.debug("Skipping tslint execution - " + TypeScriptPlugin.SETTING_TS_LINT_ENABLED + " set to false");
            return;
        }

        TsLintExecutorConfig config = TsLintExecutorConfig.fromSettings(this.settings, ctx, this.resolver);

        if (!config.useExistingTsLintOutput()) {
            if (config.getPathToTsLint() == null) {
                LOG.warn("Path to tslint not defined or not found. Skipping tslint analysis.");
                return;
            } else {
                if (config.getConfigFile() == null && config.getPathToTsConfig() == null) {
                    LOG.warn("Path to tslint.json and tsconfig.json configuration files either not defined or not found - at least one is required. Skipping tslint analysis.");
                    return;
                }
            }
        }

        boolean skipTypeDefFiles = settings.getBoolean(TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES);

        Collection<ActiveRule> allRules = ctx.activeRules().findByRepository(TsRulesDefinition.REPOSITORY_NAME);
        HashSet<String> ruleNames = new HashSet<>();
        for (ActiveRule rule : allRules) {
            ruleNames.add(rule.ruleKey().rule());
        }

        List<String> paths = new ArrayList<>();

        for (InputFile file : ctx.fileSystem().inputFiles(ctx.fileSystem().predicates().hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            if (shouldSkipFile(file.file(), skipTypeDefFiles)) {
                continue;
            }

            String pathAdjusted = file.absolutePath();
            paths.add(pathAdjusted);
        }

        List<String> jsonResults = this.executor.execute(config, paths);

        Map<String, List<TsLintIssue>> issues = this.parser.parse(jsonResults);

        if (issues == null) {
            LOG.warn("TsLint returned no result at all");
            return;
        }

        // Each issue bucket will contain info about a single file
        for (Map.Entry<String, List<TsLintIssue>> kvp : issues.entrySet()) {
            String filePath = kvp.getKey();
            List<TsLintIssue> batchIssues = kvp.getValue();

            if (batchIssues == null || batchIssues.isEmpty()) {
                continue;
            }

            File matchingFile = ctx.fileSystem().resolvePath(filePath);
            InputFile inputFile = null;

            if (shouldSkipFile(matchingFile, skipTypeDefFiles)) {
                continue;
            }

            if (matchingFile != null) {
                try {
                    inputFile = ctx.fileSystem().inputFile(ctx.fileSystem().predicates().is(matchingFile));
                }
                catch (IllegalArgumentException e) {
                    LOG.error("Failed to resolve " + filePath + " to a single path", e);
                    continue;
                }
            }

            if (inputFile == null) {
                LOG.warn("TsLint reported issues against a file that isn't in the analysis set - will be ignored: " + filePath);
                continue;
            }
            else {
                LOG.debug("Handling TsLint output for '" + filePath + "' reporting against '" + inputFile.absolutePath() + "'");
            }

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
                        .on(inputFile)
                        .message(issue.getFailure())
                        .at(inputFile.selectLine(issue.getStartPosition().getLine() + 1));

                newIssue.at(newIssueLocation);
                newIssue.save();
            }
        }
    }

    private boolean shouldSkipFile(File f, boolean skipTypeDefFiles) {
        return skipTypeDefFiles && f.getName().toLowerCase().endsWith("." + TypeScriptLanguage.LANGUAGE_DEFINITION_EXTENSION);
    }
}
