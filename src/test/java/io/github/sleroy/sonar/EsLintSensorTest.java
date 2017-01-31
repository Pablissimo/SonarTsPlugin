package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.EsLintExecutor;
import io.github.sleroy.sonar.api.EsLintParser;
import io.github.sleroy.sonar.api.PathResolver;
import io.github.sleroy.sonar.model.EsLintIssue;
import io.github.sleroy.sonar.model.EsLintPosition;
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
        this.fakePathResolutions = new HashMap<String, String>();
        this.fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_PATH, "/path/to/eslint");
        this.fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH, "/path/to/.eslintrc.json");
        this.fakePathResolutions.put(EsLintPlugin.SETTING_ES_LINT_RULES_DIR, "/path/to/rules");
        
        this.settings = mock(Settings.class);
        when(this.settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)).thenReturn(45000);
        when(this.settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)).thenReturn(true);
        this.executor = mock(EsLintExecutor.class);
        this.parser = mock(EsLintParser.class);

        this.resolver = mock(PathResolver.class);
        this.sensor = spy(new EsLintSensor(settings, this.resolver, this.executor, this.parser));

        this.file = new DefaultInputFile("", "path/to/file")
                        .setLanguage(EsLintLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });
        
        this.typeDefFile = new DefaultInputFile("", "path/to/file.d.ts")
                        .setLanguage(EsLintLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });
                
        this.context = SensorContextTester.create(new File(""));
        this.context.fileSystem().add(this.file);
        this.context.fileSystem().add(this.typeDefFile);
        
        ActiveRulesBuilder rulesBuilder = new ActiveRulesBuilder();
        rulesBuilder.create(RuleKey.of(EsRulesDefinition.REPOSITORY_NAME, "rule name")).activate();
        
        this.context.setActiveRules(rulesBuilder.build());
        
        // Pretend all paths are absolute
        Answer<String> lookUpFakePath = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return fakePathResolutions.get(invocation.<String>getArgument(1));
            }   
        };

        doAnswer(lookUpFakePath).when(this.resolver).getPath(any(SensorContext.class), any(String.class), any());
        
        this.configCaptor = ArgumentCaptor.forClass(EsLintExecutorConfig.class);
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
        
        assertEquals(EsLintLanguage.LANGUAGE_KEY, desc.languages().iterator().next());
    }
    
    @Test
    public void execute_addsIssues() {        
        EsLintIssue issue = new EsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.file.absolutePath().replace("\\",  "/"));

        EsLintPosition startPosition = new EsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
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
        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(this.file.absolutePath().replace("\\",  "/"), null);
        when(this.parser.parse(any(List.class))).thenReturn(issues);
        
        this.sensor.execute(this.context);
    }
    
    @Test
    public void execute_doesNotThrow_ifFileIssuesEmpty() {
        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(this.file.absolutePath().replace("\\",  "/"), new ArrayList<EsLintIssue>());
        when(this.parser.parse(any(List.class))).thenReturn(issues);
        
        this.sensor.execute(this.context);
    }    

    @Test
    public void execute_addsToUnknownRuleBucket_whenRuleNameNotRecognised() {
        EsLintIssue issue = new EsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("unknown name");
        issue.setName(this.file.absolutePath().replace("\\",  "/"));

        EsLintPosition startPosition = new EsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(issue.getName(), issueList);
        
        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);
        
        assertEquals(1, this.context.allIssues().size());
        assertEquals(EsRulesDefinition.ESLINT_UNKNOWN_RULE.key, this.context.allIssues().iterator().next().ruleKey().rule());
    }
    
    @Test
    public void execute_doesNotThrow_ifTsLintReportsAgainstFileNotInAnalysisSet() {
        EsLintIssue issue = new EsLintIssue();
        issue.setFailure("failure");
        issue.setRuleName("rule name");
        issue.setName(this.file.absolutePath().replace("\\",  "/") + "/nonexistent");

        EsLintPosition startPosition = new EsLintPosition();
        startPosition.setLine(0);

        issue.setStartPosition(startPosition);

        List<EsLintIssue> issueList = new ArrayList<EsLintIssue>();
        issueList.add(issue);

        Map<String, List<EsLintIssue>> issues = new HashMap<String, List<EsLintIssue>>();
        issues.put(issue.getName(), issueList);
        
        when(this.parser.parse(any(List.class))).thenReturn(issues);
        this.sensor.execute(this.context);        
    }
    


    @Test
    public void execute_doesNothingWhenNotConfigured() throws IOException {
        this.fakePathResolutions.remove(EsLintPlugin.SETTING_ES_LINT_PATH);

        this.sensor.execute(this.context);
        
        verify(this.executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));
        
        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void analyse_doesNothingWhenDisabled() throws IOException {
        when(this.settings.getBoolean(EsLintPlugin.SETTING_ES_LINT_ENABLED)).thenReturn(Boolean.FALSE);
    
        this.sensor.execute(this.context);
        
        verify(this.executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));
        
        assertEquals(0, this.context.allIssues().size());
    }
    
    @Test
    public void execute_doesNothingWhenNoConfigPathset() throws IOException {
        this.fakePathResolutions.remove(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH);
        
        this.sensor.execute(this.context);
        
        verify(this.executor, times(0)).execute(any(EsLintExecutorConfig.class), any(List.class));
        
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
        when(this.settings.getInt(EsLintPlugin.SETTING_ES_LINT_TIMEOUT)).thenReturn(-500);
        
        this.sensor.execute(this.context);
        
        verify(this.executor, times(1)).execute(this.configCaptor.capture(), any(List.class));
        assertEquals((Integer) 5000, this.configCaptor.getValue().getTimeoutMs());
    }

    @Test
    public void execute_callsExecutorWithConfiguredPaths() {
        this.sensor.execute(this.context);
        
        verify(this.executor, times(1)).execute(this.configCaptor.capture(), any(List.class));
        assertEquals("/path/to/eslint", this.configCaptor.getValue().getPathToEsLint());
        assertEquals("/path/to/.eslintrc.json", this.configCaptor.getValue().getConfigFile());
        assertEquals("/path/to/rules", this.configCaptor.getValue().getRulesDir());
    }
}
