package io.github.sleroy.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Settings;

public class EsLintExecutorConfigTest {
    
    private EsLintExecutorConfig getNewConfig() {
        return new EsLintExecutorConfig();
    }
    
    @Test
    public void canGetSetPathToTsLint() {
        EsLintExecutorConfig config = getNewConfig();
        config.setPathToTsLint("My path");
        
        assertEquals("My path",  config.getPathToTsLint());
    }
    
    @Test
    public void canGetSetPathToTsLintConfig() {
        EsLintExecutorConfig config = getNewConfig();
        config.setConfigFile("My path");
        
        assertEquals("My path",  config.getConfigFile());
    }
    
    @Test
    public void canGetSetRulesDir() {
        EsLintExecutorConfig config = getNewConfig();
        config.setRulesDir("My path");
        
        assertEquals("My path",  config.getRulesDir());
    }
    
    @Test
    public void canGetSetTimeout() {
        EsLintExecutorConfig config = getNewConfig();
        config.setTimeoutMs(12);
        
        assertEquals((Integer) 12, config.getTimeoutMs());
    }
    
    @Test
    public void canGetSetTsConfigPath() {
        EsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("My path");
        
        assertEquals("My path",  config.getPathToTsConfig());
    }
    
    @Test
    public void canGetSetTypeCheck() {
        EsLintExecutorConfig config = getNewConfig();
        config.setShouldPerformTypeCheck(true);
        
        assertTrue(config.shouldPerformTypeCheck());
    }
    
    @Test
    public void useTsConfigInsteadOfFileList_returnsTrue_ifPathToTsConfigSet() {
        EsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("My path");
        
        assertTrue(config.useTsConfigInsteadOfFileList());
    }
    
    @Test
    public void useTsConfigInsteadOfFileList_returnsFalse_ifPathToTsConfigNotSet() {
        EsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("");
        
        assertFalse(config.useTsConfigInsteadOfFileList());
    }
    
    @Test
    public void fromSettings_initialisesFromSettingsAndResolver() {
        Settings settings = new Settings();
        settings.setProperty(EsLintPlugin.SETTING_ES_LINT_TIMEOUT, 12000);
        settings.setProperty(EsLintPlugin.SETTING_TS_LINT_TYPECHECK, true);
        
        PathResolver resolver = mock(PathResolver.class);
        
        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_PATH),
                               eq(EsLintExecutorConfig.TSLINT_FALLBACK_PATH))
        ).thenReturn("tslint");
        
        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH),
                               eq(EsLintExecutorConfig.CONFIG_FILENAME))
        ).thenReturn("tslint.json");

        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_RULES_DIR),
                               eq((String) null))
        ).thenReturn("rulesdir");
        
        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_PROJECT_PATH),
                               eq((String) null))
        ).thenReturn("tsconfig.json");
        
        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(settings, SensorContextTester.create(new File("")), resolver);
        
        assertEquals("tslint", config.getPathToTsLint());
        assertEquals("tslint.json", config.getConfigFile());
        assertEquals("rulesdir", config.getRulesDir());
        assertEquals("tsconfig.json", config.getPathToTsConfig());
        
        assertTrue(config.shouldPerformTypeCheck());
        assertEquals((Integer) 12000, config.getTimeoutMs());
    }
    
    @Test
    public void fromSettings_setsTimeoutTo5000msMinimum_ifSetToLess() {
        Settings settings = new Settings();
        settings.setProperty(EsLintPlugin.SETTING_ES_LINT_TIMEOUT, 1000);
        
        PathResolver resolver = mock(PathResolver.class);

        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(settings, SensorContextTester.create(new File("")), resolver);

        assertEquals((Integer) 5000, config.getTimeoutMs());
    }
}
