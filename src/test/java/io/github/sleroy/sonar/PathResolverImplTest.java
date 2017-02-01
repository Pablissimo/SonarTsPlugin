package io.github.sleroy.sonar;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class PathResolverImplTest {
    private PathResolverImpl resolver;
    private SensorContextTester sensorContext;

    private File existingFile;

    @Before
    public void setUp() throws Exception {
        URL filePath = PathResolverImplTest.class.getClassLoader().getResource("./existing.ts");
        this.existingFile = new File(filePath.toURI());
        String parentPath = this.existingFile.getParent();

        sensorContext = SensorContextTester.create(new File(parentPath));
        sensorContext.settings().setProperty("path key", "existing.ts");
        
        DefaultInputFile file = 
                new DefaultInputFile("", "existing.ts")
                    .setLanguage(EsLintLanguage.LANGUAGE_KEY);

        sensorContext.fileSystem().add(file);

        resolver = new PathResolverImpl();
    }
    
    @Test
    public void returnsAbsolutePathToFile_ifSpecifiedAndExists() {

        this.assertSamePath(existingFile, resolver.getPathFromSetting(sensorContext, "path key", "not me"));
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredAndFallbackExists() {
        this.assertSamePath(existingFile, resolver.getPathFromSetting(sensorContext, "new path key", "existing.ts"));
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredButEmptyAndFallbackExists() {
        sensorContext.settings().setProperty("new path key", "");
        this.assertSamePath(existingFile, resolver.getPathFromSetting(sensorContext, "new path key", "existing.ts"));
    }
    
    @Test
    public void returnsNull_ifPrimaryNotConfiguredAndFallbackNull() {
        assertNull(resolver.getPathFromSetting(sensorContext, "new path key", null));
    }
    
    @Test
    public void returnsNull_ifRequestedPathDoesNotExist() {
        sensorContext.settings().setProperty("new path key", "missing.ts");
        assertNull(resolver.getPathFromSetting(sensorContext, "new path key", "existing.ts"));
    }
    
    @Test
    public void returnsAbsolutePathToFile_ifAlreadyAbsoluteAndExists() {
        sensorContext.settings().setProperty("new path key", existingFile.getAbsolutePath());
        this.assertSamePath(existingFile, resolver.getPathFromSetting(sensorContext, "new path key", "not me"));
    }

    /**
     * Asserts that the provided path is the same as the argument. Paths are converted in File objects to avoid case-sensitive issues on some filesystems.
     * @param existingFile the path to test against
     * @param argument the argument
     */
    private void assertSamePath(File existingFile, Optional<String> argument) {
        if (!argument.isPresent())
            assertNull("File should not exists", existingFile);
        else {
            assertEquals(existingFile, argument.map(f -> new File(f)).get());
        }
    }
}
