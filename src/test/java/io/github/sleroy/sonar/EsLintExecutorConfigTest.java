package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.assertj.core.util.Files;
import org.junit.Test;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.config.Settings;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EsLintExecutorConfigTest {
    
    private EsLintExecutorConfig getNewConfig() {
        return new EsLintExecutorConfig();
    }
    
    @Test
    public void canGetSetPathToTsLint() {
        EsLintExecutorConfig config = getNewConfig();
        config.setPathToEsLint("My path");

        assertEquals("My path", config.getPathToEsLint());
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
    public <T> void fromSettings_testDefaultValues() {
        Settings settings = new Settings();
        settings.setProperty(EsLintPlugin.SETTING_ES_LINT_TIMEOUT, 12000);

        FileSystem fileSystemMock = mock(FileSystem.class);
        SensorContext sensorContextMock = mock(SensorContext.class);
        when(sensorContextMock.settings()).thenReturn(settings);
        when(sensorContextMock.fileSystem()).thenReturn(fileSystemMock);
        when(fileSystemMock.baseDir()).thenReturn(Files.currentFolder());
        PathResolver pathResolver = new PathResolverImpl();

        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(settings, sensorContextMock, pathResolver);
        assertNotEquals("Eslint is not installed locally", EsLintExecutorConfig.ESLINT_FALLBACK_PATH, config.getPathToEsLint());
        assertNotEquals("No local Eslint file", EsLintExecutorConfig.CONFIG_FILENAME, config.getConfigFile());
        assertNull(config.getRulesDir());
        assertEquals((Integer) 12000, config.getTimeoutMs());
    }

    
    @Test
    public void fromSettings_initialisesFromSettingsAndResolver() {
        Settings settings = new Settings();
        settings.setProperty(EsLintPlugin.SETTING_ES_LINT_TIMEOUT, 12000);


        PathResolver resolver = mock(PathResolver.class);
        
        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_PATH),
                eq(EsLintExecutorConfig.ESLINT_FALLBACK_PATH))
        ).thenReturn("eslint");
        
        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH),
                               eq(EsLintExecutorConfig.CONFIG_FILENAME))
        ).thenReturn(".eslintrc.json");

        when(resolver.getPath(any(SensorContext.class), 
                               eq(EsLintPlugin.SETTING_ES_LINT_RULES_DIR),
                eq(null))
        ).thenReturn("rulesdir");


        EsLintExecutorConfig config = EsLintExecutorConfig.fromSettings(settings, SensorContextTester.create(new File("")), resolver);

        assertEquals("eslint", config.getPathToEsLint());
        assertEquals(".eslintrc.json", config.getConfigFile());
        assertEquals("rulesdir", config.getRulesDir());


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
