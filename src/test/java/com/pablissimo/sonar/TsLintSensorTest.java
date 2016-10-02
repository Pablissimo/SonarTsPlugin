package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
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
import org.sonar.api.utils.System2;

public class TsLintSensorTest {
    Settings settings;
    
    DefaultInputFile file;

    TsLintExecutor executor;
    TsLintParser parser;
    TsLintSensor sensor;

    SensorContextTester context;
    
    PathResolver resolver;
    HashMap<String, String> fakePathResolutions;
    
    System2 system;
    
    @Before
    public void setUp() throws Exception {
        this.fakePathResolutions = new HashMap<String, String>();
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_PATH, "/path/to/tslint");
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH, "/path/to/tslint.json");
        this.fakePathResolutions.put(TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR, "/path/to/rules");
        
        this.settings = mock(Settings.class);
        this.system = mock(System2.class);
        
        when(this.settings.getInt(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT)).thenReturn(45000);
        this.executor = mock(TsLintExecutor.class);
        this.parser = mock(TsLintParser.class);
        this.resolver = mock(PathResolver.class);
        this.sensor = spy(new TsLintSensor(settings, this.system));

        this.file = new DefaultInputFile("", "path/to/file")
                        .setLanguage(TypeScriptLanguage.LANGUAGE_KEY)
                        .setLines(1)
                        .setLastValidOffset(999)
                        .setOriginalLineOffsets(new int[] { 5 });
        
        doReturn(this.executor).when(this.sensor).getTsLintExecutor();
        doReturn(this.parser).when(this.sensor).getTsLintParser();
        doReturn(this.resolver).when(this.sensor).getPathResolver();
        
        this.context = SensorContextTester.create(new File(""));
        this.context.fileSystem().add(this.file);      
        
        ActiveRulesBuilder rulesBuilder = new ActiveRulesBuilder();
        rulesBuilder.create(RuleKey.of(TsRulesDefinition.REPOSITORY_NAME, "rule name")).activate();
        
        this.context.setActiveRules(rulesBuilder.build());
        
        // Pretend all paths are absolute
        Answer<String> lookUpFakePath = new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return fakePathResolutions.get(invocation.getArgumentAt(1, String.class));
            }   
        };
        
        doAnswer(lookUpFakePath).when(this.resolver).getPath(any(SensorContext.class), any(String.class), any(String.class));
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

        TsLintIssue[][] issues = new TsLintIssue[][] {
                new TsLintIssue[] { issue }
        };
        
        when(this.parser.parse(any(String.class))).thenReturn(issues);
        this.sensor.execute(this.context);
        
        assertEquals(1, this.context.allIssues().size());
        assertEquals("rule name", this.context.allIssues().iterator().next().ruleKey().rule());
    }

    @Test
    public void execute_doesNothingWhenNotConfigured() throws IOException {
        this.fakePathResolutions.remove(TypeScriptPlugin.SETTING_TS_LINT_PATH);

        this.sensor.execute(this.context);
        
        verify(this.executor, times(0)).execute(any(String.class), any(String.class), any(String.class), any(List.class), any(Integer.class));
        
        assertEquals(0, this.context.allIssues().size());
    }
    
    @Test
    public void execute_doesNothingWhenNoConfigPathset() throws IOException {
        this.fakePathResolutions.remove(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH);
        
        this.sensor.execute(this.context);
        
        verify(this.executor, times(0)).execute(any(String.class), any(String.class), any(String.class), any(List.class), any(Integer.class));
        
        assertEquals(0, this.context.allIssues().size());
    }

    @Test
    public void execute_callsExecutorWithSuppliedTimeout() throws IOException {
        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(any(String.class), any(String.class), any(String.class), any(List.class), eq(45000));
    }

    @Test
    public void execute_callsExecutorWithAtLeast5000msTimeout() throws IOException {
        when(this.settings.getInt(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT)).thenReturn(-500);
        
        this.sensor.execute(this.context);

        verify(this.executor, times(1)).execute(any(String.class), any(String.class), any(String.class), any(List.class), eq(5000));
    }

    @Test
    public void execute_callsExecutorWithConfiguredPaths() {
        this.sensor.execute(this.context);
        
        verify(this.executor, times(1)).execute(eq("/path/to/tslint"), eq("/path/to/tslint.json"), eq("/path/to/rules"), any(List.class), any(Integer.class));
    }
}
