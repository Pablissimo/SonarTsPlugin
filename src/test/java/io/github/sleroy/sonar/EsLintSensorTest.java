package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.EsLintExecutor;
import io.github.sleroy.sonar.api.EsLintParser;
import io.github.sleroy.sonar.api.PathResolver;
import io.github.sleroy.sonar.model.EsLintIssue;
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
import org.sonar.api.rule.RuleKey;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class EsLintSensorTest {
    Settings settings;
    
    DefaultInputFile file;
    DefaultInputFile typeDefFile;

    EsLintExecutor executor;
    EsLintParser parser;
    EsLintSensor sensor;

    SensorContextTester context;
    
    PathResolver resolver;
    HashMap<String, String> fakePathResolutions;
        
    ArgumentCaptor<EsLintExecutorConfig> configCaptor;
    
    @Before
    public void setUp() throws Exception {
        fakePathResolutions = new HashMap<String, String>();
        fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_PATH, "/path/to/eslint");
        fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, "src/test/resources/.eslintrc.js");
        fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_RULES_DIR, "/path/to/rules");

        settings = mock(Settings.class);
        when(settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)).thenReturn(45000);
        when(settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)).thenReturn(true);
        executor = mock(EsLintExecutor.class);
        parser = mock(EsLintParser.class);

        resolver = mock(PathResolver.class);
        sensor = spy(new EsLintSensor(this.settings, resolver, executor, parser));

        file = new DefaultInputFile("", "path/to/file")
                        .setLanguage(EsLintLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });

        typeDefFile = new DefaultInputFile("", "path/to/file.d.ts")
                        .setLanguage(EsLintLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });

        context = SensorContextTester.create(new File(""));
        context.fileSystem().add(file);
        context.fileSystem().add(typeDefFile);
        
        ActiveRulesBuilder rulesBuilder = new ActiveRulesBuilder();
        rulesBuilder.create(RuleKey.of(EsRulesDefinition.REPOSITORY_NAME, "rule name")).activate();

        context.setActiveRules(rulesBuilder.build());
        
        // Pretend all paths are absolute
        Answer<Optional<String>> lookUpFakePath = new Answer<Optional<String>>() {
            @Override
            public Optional<String> answer(InvocationOnMock invocation) throws Throwable {
                return Optional.ofNullable(EsLintSensorTest.this.fakePathResolutions.get(invocation.<String>getArgument(1)));
            }   
        };
        doAnswer(lookUpFakePath).when(resolver).getPathFromSetting(any(SensorContext.class), any(String.class), any());

        // Pretend all paths are absolute
        Answer<Optional<String>> lookUpFakePath2 = new Answer<Optional<String>>() {
            @Override
            public Optional<String> answer(InvocationOnMock invocation) throws Throwable {
                return Optional.ofNullable(EsLintSensorTest.this.fakePathResolutions.get(invocation.<String>getArgument(1)));
            }
        };
        doAnswer(lookUpFakePath2).when(resolver).getPathFromSetting(any(SensorContext.class), any(String.class));


        configCaptor = ArgumentCaptor.forClass(EsLintExecutorConfig.class);
    }

    @Test
    public void describe_setsName() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        sensor.describe(desc);
        
        assertNotNull(desc.name());
    }
    
    @Test
    public void describe_setsLanguage() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        sensor.describe(desc);
        
        assertEquals(EsLintLanguage.LANGUAGE_KEY, desc.languages().iterator().next());
    }
    
    @Test
    public void execute_addsIssues() {        
        EsLintIssue issue = new EsLintIssue();
        issue.setMessage("failure");
        issue.setRuleId("rule name");
        issue.setName(file.absolutePath().replace("\\", "/"));


        issue.setLine(0);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(parser.parse(any(List.class))).thenReturn(issues);
        sensor.execute(context);

        assertEquals(1, context.allIssues().size());
        assertEquals("rule name", context.allIssues().iterator().next().ruleKey().rule());
    }
    
    @Test
    public void execute_doesNotThrow_ifParserReturnsNoResult() {
        when(parser.parse(any(List.class))).thenReturn(null);

        sensor.execute(context);
    }
    
    @Test
    public void execute_doesNotThrow_ifFileIssuesNull() {
        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(file.absolutePath().replace("\\", "/"), null);
        when(parser.parse(any(List.class))).thenReturn(issues);

        sensor.execute(context);
    }
    
    @Test
    public void execute_doesNotThrow_ifFileIssuesEmpty() {
        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(file.absolutePath().replace("\\", "/"), new ArrayList<EsLintIssue>());
        when(parser.parse(any(List.class))).thenReturn(issues);

        sensor.execute(context);
    }    

    @Test
    public void execute_addsToUnknownRuleBucket_whenRuleNameNotRecognised() {
        EsLintIssue issue = new EsLintIssue();
        issue.setMessage("failure");
        issue.setRuleId("unknown name");
        issue.setName(file.absolutePath().replace("\\", "/"));


        issue.setLine(0);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(parser.parse(any(List.class))).thenReturn(issues);
        sensor.execute(context);

        assertEquals(1, context.allIssues().size());
        assertEquals(EsRulesDefinition.ESLINT_UNKNOWN_RULE.getKey(), context.allIssues().iterator().next().ruleKey().rule());
    }
    
    @Test
    public void execute_doesNotThrow_ifTsLintReportsAgainstFileNotInAnalysisSet() {
        EsLintIssue issue = new EsLintIssue();
        issue.setMessage("failure");
        issue.setRuleId("rule name");
        issue.setName(file.absolutePath().replace("\\", "/") + "/nonexistent");


        issue.setLine(0);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(issue.getName(), issueList);

        when(parser.parse(any(List.class))).thenReturn(issues);
        sensor.execute(context);
    }
    


    @Test
    public void execute_doesNothingWhenNotConfigured() throws IOException {
        fakePathResolutions.remove(EsLintPlugin.SETTING_ES_LINT_PATH);

        sensor.execute(context);

        verify(executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));

        assertEquals(0, context.allIssues().size());
    }

    @Test
    public void analyse_doesNothingWhenDisabled() throws IOException {
        when(settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)).thenReturn(Boolean.FALSE);

        sensor.execute(context);

        verify(executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));

        assertEquals(0, context.allIssues().size());
    }
    
    @Test
    public void execute_whenThePathDoesNotExist() throws IOException {
        fakePathResolutions.remove(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH);

        sensor.execute(context);

        verify(executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));

        assertEquals(0, context.allIssues().size());
    }

    @Test
    public void execute_callsExecutorWithSuppliedTimeout() throws IOException {
        sensor.execute(context);

        verify(executor, times(1)).execute(configCaptor.capture(), any(List.class));
        assertEquals((Integer) 45000, configCaptor.getValue().getTimeoutMs());
    }

    @Test
    public void execute_callsExecutorWithAtLeast5000msTimeout() throws IOException {
        when(settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)).thenReturn(-500);

        sensor.execute(context);

        verify(executor, times(1)).execute(configCaptor.capture(), any(List.class));
        assertEquals((Integer) 5000, configCaptor.getValue().getTimeoutMs());
    }

    @Test
    public void execute_callsExecutorWithConfiguredPaths() {
        sensor.execute(context);

        verify(executor, times(1)).execute(configCaptor.capture(), any(List.class));
        assertEquals("/path/to/eslint", configCaptor.getValue().getPathToEsLint());
        assertEquals("src/test/resources/.eslintrc.js", configCaptor.getValue().getConfigFile());
        assertEquals("/path/to/rules", configCaptor.getValue().getRulesDir());
    }
}
