package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.EsLintExecutor;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.sonar.api.utils.System2;
import org.sonar.api.utils.TempFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

/**
 * Integration test for EsLint
 */
@Category(IntegrationTest.class)
@RunWith(value = MockitoJUnitRunner.class)
public class EsLintExecutorIntegrationTest {

    @Mock
    System2 system;

    @Mock
    TempFolder tempFolder;

    @Ignore("ESLint Integration debug test")
    @Test
    public void testEsLint() throws IOException {

        EsLintExecutorConfig esLintConfiguration = new EsLintExecutorConfig();
        esLintConfiguration.setConfigFile("src\\test\\resources\\.eslintrc.js");
        esLintConfiguration.setPathToEsLint("C:\\Users\\Administrator\\AppData\\Roaming\\npm\\node_modules\\eslint\\bin\\eslint.js");
        esLintConfiguration.setTimeoutMs(40000);


        when(tempFolder.newFile()).thenReturn(File.createTempFile("eslintexecutor", ".json"));

        EsLintExecutor esLintExecutor = new EsLintExecutorImpl(system, tempFolder);

        List<String> files = new ArrayList<>();
        files.add(new File("src/test/resources/dashboard.js").getAbsolutePath());

        List<String> commandOutput = esLintExecutor.execute(esLintConfiguration, files);
        assertNotNull(commandOutput);
        assertEquals("Expected number of results", 1, commandOutput.size());
    }


}
