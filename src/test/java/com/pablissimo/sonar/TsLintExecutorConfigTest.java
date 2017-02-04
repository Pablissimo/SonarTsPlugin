package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Test;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Settings;

public class TsLintExecutorConfigTest {

    private TsLintExecutorConfig getNewConfig() {
        return new TsLintExecutorConfig();
    }

    @Test
    public void canGetSetPathToTsLint() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsLint("My path");

        assertEquals("My path",  config.getPathToTsLint());
    }

    @Test
    public void canGetSetPathToTsLintConfig() {
        TsLintExecutorConfig config = getNewConfig();
        config.setConfigFile("My path");

        assertEquals("My path",  config.getConfigFile());
    }

    @Test
    public void canGetSetRulesDir() {
        TsLintExecutorConfig config = getNewConfig();
        config.setRulesDir("My path");

        assertEquals("My path",  config.getRulesDir());
    }

    @Test
    public void canGetSetTimeout() {
        TsLintExecutorConfig config = getNewConfig();
        config.setTimeoutMs(12);

        assertEquals((Integer) 12, config.getTimeoutMs());
    }

    @Test
    public void canGetSetTsConfigPath() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("My path");

        assertEquals("My path",  config.getPathToTsConfig());
    }

    @Test
    public void canGetSetTypeCheck() {
        TsLintExecutorConfig config = getNewConfig();
        config.setShouldPerformTypeCheck(true);

        assertTrue(config.shouldPerformTypeCheck());
    }

    @Test
    public void useTsConfigInsteadOfFileList_returnsTrue_ifPathToTsConfigSet() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("My path");

        assertTrue(config.useTsConfigInsteadOfFileList());
    }

    @Test
    public void useTsConfigInsteadOfFileList_returnsFalse_ifPathToTsConfigNotSet() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsConfig("");

        assertFalse(config.useTsConfigInsteadOfFileList());
    }
    
    @Test
    public void useExistingTsLintOutput_returnsTrueIfPathSet() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsLintOutput("/path/to/tslint/json/output");
        
        assertTrue(config.useExistingTsLintOutput());
    }
    
    @Test
    public void useExistingTsLintOutput_returnsFalseIfPathNotSet() {
        TsLintExecutorConfig config = getNewConfig();
        config.setPathToTsLintOutput("");
        
        assertFalse(config.useExistingTsLintOutput());
    }

    @Test
    public void fromSettings_initialisesFromSettingsAndResolver() {
        Settings settings = new Settings();
        settings.setProperty(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT, 12000);
        settings.setProperty(TypeScriptPlugin.SETTING_TS_LINT_TYPECHECK, true);

        PathResolver resolver = mock(PathResolver.class);

        when(resolver.getPath(any(SensorContext.class),
                               eq(TypeScriptPlugin.SETTING_TS_LINT_PATH),
                               eq(TsLintExecutorConfig.TSLINT_FALLBACK_PATH))
        ).thenReturn("tslint");

        when(resolver.getPath(any(SensorContext.class),
                               eq(TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH),
                               eq(TsLintExecutorConfig.CONFIG_FILENAME))
        ).thenReturn("tslint.json");

        when(resolver.getPath(any(SensorContext.class),
                               eq(TypeScriptPlugin.SETTING_TS_LINT_RULES_DIR),
                               eq((String) null))
        ).thenReturn("rulesdir");

        when(resolver.getPath(any(SensorContext.class),
                               eq(TypeScriptPlugin.SETTING_TS_LINT_PROJECT_PATH),
                               eq((String) null))
        ).thenReturn("tsconfig.json");

        when(resolver.getPath(any(SensorContext.class),
                                eq(TypeScriptPlugin.SETTING_TS_LINT_OUTPUT_PATH),
                                eq((String) null))
        ).thenReturn("out.json");

        TsLintExecutorConfig config = TsLintExecutorConfig.fromSettings(settings, SensorContextTester.create(new File("")), resolver);

        assertEquals("tslint", config.getPathToTsLint());
        assertEquals("tslint.json", config.getConfigFile());
        assertEquals("rulesdir", config.getRulesDir());
        assertEquals("tsconfig.json", config.getPathToTsConfig());

        assertEquals("out.json", config.getPathToTsLintOutput());
        assertTrue(config.shouldPerformTypeCheck());
        assertEquals((Integer) 12000, config.getTimeoutMs());
    }

    @Test
    public void fromSettings_setsTimeoutTo5000msMinimum_ifSetToLess() {
        Settings settings = new Settings();
        settings.setProperty(TypeScriptPlugin.SETTING_TS_LINT_TIMEOUT, 1000);

        PathResolver resolver = mock(PathResolver.class);

        TsLintExecutorConfig config = TsLintExecutorConfig.fromSettings(settings, SensorContextTester.create(new File("")), resolver);

        assertEquals((Integer) 5000, config.getTimeoutMs());
    }
}
