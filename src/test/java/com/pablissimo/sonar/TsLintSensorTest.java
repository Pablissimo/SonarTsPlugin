package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.rule.internal.ActiveRulesBuilder;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Settings;
import com.pablissimo.sonar.model.TsLintIssue;
import com.pablissimo.sonar.model.TsLintPosition;

import org.sonar.api.rule.RuleKey;

public class TsLintSensorTest {
    Settings settings;

    DefaultInputFile file;
    DefaultInputFile typeDefFile;

    TsLintExecutor executor;
    TsLintParser parser;
    TsLintSensor sensor;

    SensorContextTester context;

    PathResolver resolver;
    HashMap<String, String> fakePathResolutions;

    ArgumentCaptor<TsLintExecutorConfig> configCaptor;

    @Before
    public void setUp() throws Exception {
        this.fakePathResolutions = new HashMap<String, String>();
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_PATH, "/path/to/tslint");
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH, "/path/to/tslint.json");
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR, "/path/to/rules");

        this.settings = mock(Settings.class);
        when(this.settings.getInt(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT)).thenReturn(45000);
        when(this.settings.getBoolean(TypeScriptPlugin.SETTING_TS_LINT_ENABLED)).thenReturn(true);
        this.executor = mock(TsLintExecutor.class);
        this.parser = mock(TsLintParser.class);

        this.resolver = mock(PathResolver.class);
        this.sensor = spy(new TsLintSensor(settings, this.resolver, this.executor, this.parser));

        this.file = new DefaultInputFile("", "path/to/file")
                        .setLanguage(TypeScriptLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });

        this.typeDefFile = new DefaultInputFile("", "path/to/file.d.ts")
                        .setLanguage(TypeScriptLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });

        this.context = SensorContextTester.create(new File(""));
        this.context.fileSystem().add(this.file);
        this.context.fileSystem().add(this.typeDefFile);

        ActiveRulesBuilder rulesBuilder = new ActiveRulesBuilder();
        rulesBuilder.create(RuleKey.of(TsRulesDefinition.REPOSITORY_NAME, "rule name")).activate();

        this.context.setActiveRules(rulesBuilder.build());

        // Pretend all paths are absolute
        Answer<String> lookUpFakePath = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return fakePathResolutions.get(invocation.<String>getArgument(1));
            }
        };

        doAnswer(lookUpFakePath).when(this.resolver).getPath(any(SensorContext.class), any(String.class), (String) any());

        this.configCaptor = ArgumentCaptor.forClass(TsLintExecutorConfig.class);
    }

    @Test
    public void describe_setsName() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        this.sensor.describe(desc);

        assertNotNull(desc.name());
    }

    @Test
    public void describe_setsLanguage() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        this.sensor.describe(desc);

        assertEquals(TypeScriptLanguage.LANGUAGE_KEY, desc.languages().iterator().next());
    }

    @Test
    public void execute_addsIssues() {
        TsLintIssue issue = new TsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.file.absolutePath().replace("\\",  "/"));

        TsLintPosition startPosition = new TsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<TsLintIssue> issueList = new ArrayList<TsLintIssue>();
        issueList.add(issue);

        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);

        assertEquals(1, this.context.allIssues().size());
        assertEquals("rule name", this.context.allIssues().iterator().next().ruleKey().rule());
    }

    @Test
    public void execute_addsIssues_evenIfReportedAgainstRelativePaths() {
        TsLintIssue issue = new TsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.file.relativePath().replace("\\",  "/"));

        TsLintPosition startPosition = new TsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<TsLintIssue> issueList = new ArrayList<TsLintIssue>();
        issueList.add(issue);

        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);

        assertEquals(1, this.context.allIssues().size());
        assertEquals("rule name", this.context.allIssues().iterator().next().ruleKey().rule());
    }

    @Test
    public void execute_doesNotThrow_ifParserReturnsNoResult() {
        when(this.parser.parse(any(List.class))).thenReturn(null);

        this.sensor.execute(this.context);
    }

    @Test
    public void execute_doesNotThrow_ifFileIssuesNull() {
        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(this.file.absolutePath().replace("\\",  "/"), null);
        when(this.parser.parse(any(List.class))).thenReturn(issues);

        this.sensor.execute(this.context);
    }

    @Test
    public void execute_doesNotThrow_ifFileIssuesEmpty() {
        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(this.file.absolutePath().replace("\\",  "/"), new ArrayList<TsLintIssue>());
        when(this.parser.parse(any(List.class))).thenReturn(issues);

        this.sensor.execute(this.context);
    }

    @Test
    public void execute_addsToUnknownRuleBucket_whenRuleNameNotRecognised() {
        TsLintIssue issue = new TsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("unknown name");
        issue.setName(this.file.absolutePath().replace("\\",  "/"));

        TsLintPosition startPosition = new TsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<TsLintIssue> issueList = new ArrayList<TsLintIssue>();
        issueList.add(issue);

        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);

        assertEquals(1, this.context.allIssues().size());
        assertEquals(TsRulesDefinition.TSLINT_UNKNOWN_RULE.key, this.context.allIssues().iterator().next().ruleKey().rule());
    }

    @Test
    public void execute_doesNotThrow_ifTsLintReportsAgainstFileNotInAnalysisSet() {
        TsLintIssue issue = new TsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.file.absolutePath().replace("\\",  "/") + "/nonexistent");

        TsLintPosition startPosition = new TsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<TsLintIssue> issueList = new ArrayList<TsLintIssue>();
        issueList.add(issue);

        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);
    }

    @Test
    public void execute_ignoresTypeDefinitionFilesIfConfigured() {
        TsLintIssue issue = new TsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.typeDefFile.absolutePath().replace("\\",  "/"));

        TsLintPosition startPosition = new TsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<TsLintIssue> issueList = new ArrayList<TsLintIssue>();
        issueList.add(issue);

        Map<String, List<TsLintIssue>> issues = new HashMap<String, List<TsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(this.parser.parse(any(List.class))).thenReturn(issues);
        when(this.settings.getBoolean(TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES)).thenReturn(true);
        this.sensor.execute(this.context);

        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void execute_doesNothingWhenNotConfigured() throws IOException {
        this.fakePathResolutions.remove(TypeScriptPlugin.SETTING_TS_LINT_PATH);

        this.sensor.execute(this.context);

        verify(this.executor, times(0)).execute(any(TsLintExecutorConfig.class), any(List.class));

        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void analyse_doesNothingWhenDisabled() throws IOException {
        when(this.settings.getBoolean(TypeScriptPlugin.SETTING_TS_LINT_ENABLED)).thenReturn(Boolean.FALSE);

        this.sensor.execute(this.context);

        verify(this.executor, times(0)).execute(any(TsLintExecutorConfig.class), any(List.class));

        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void execute_doesNothingWhenNoConfigPathset() throws IOException {
        this.fakePathResolutions.remove(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH);

        this.sensor.execute(this.context);

        verify(this.executor, times(0)).execute(any(TsLintExecutorConfig.class), any(List.class));

        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void execute_callsExecutorWithSuppliedTimeout() throws IOException {
        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(this.configCaptor.capture(), any(List.class));
        assertEquals((Integer) 45000, this.configCaptor.getValue().getTimeoutMs());
    }

    @Test
    public void execute_callsExecutorWithAtLeast5000msTimeout() throws IOException {
        when(this.settings.getInt(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT)).thenReturn(-500);

        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(this.configCaptor.capture(), any(List.class));
        assertEquals((Integer) 5000, this.configCaptor.getValue().getTimeoutMs());
    }

    @Test
    public void execute_callsExecutorWithConfiguredPaths() {
        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(this.configCaptor.capture(), any(List.class));
        assertEquals("/path/to/tslint", this.configCaptor.getValue().getPathToTsLint());
        assertEquals("/path/to/tslint.json", this.configCaptor.getValue().getConfigFile());
        assertEquals("/path/to/rules", this.configCaptor.getValue().getRulesDir());
    }

    @Test
    public void execute_callsExecutorWithTslintOutput() throws IOException {
        this.fakePathResolutions.remove(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH);
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_OUTPUT_PATH, "/path/to/output");

        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(any(TsLintExecutorConfig.class), any(List.class));

        assertEquals(0, this.context.allIssues().size());
    }

}
