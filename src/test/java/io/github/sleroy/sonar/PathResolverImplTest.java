package io.github.sleroy.sonar;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

import java.io.File;
import java.net.URL;
import java.util.Optional;

import static org.junit.Assert.*;

public class PathResolverImplTest {
    private PathResolverImpl resolver;
    private SensorContextTester sensorContext;

    private File existingFile;

    @Before
    public void setUp() throws Exception {
        URL filePath = PathResolverImplTest.class.getClassLoader().getResource("./existing.ts");
        existingFile = new File(filePath.toURI());
        String parentPath = existingFile.getParent();

        this.sensorContext = SensorContextTester.create(new File(parentPath));
        this.sensorContext.settings().setProperty("path key", "existing.ts");
        
        DefaultInputFile file = 
                new DefaultInputFile("", "existing.ts")
                    .setLanguage(EsLintLanguage.LANGUAGE_KEY);

        this.sensorContext.fileSystem().add(file);

        this.resolver = new PathResolverImpl();
    }
    
    @Test
    public void returnsAbsolutePathToFile_ifSpecifiedAndExists() {

        assertSamePath(this.existingFile, this.resolver.getPathFromSetting(this.sensorContext, "path key", "not me"));
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredAndFallbackExists() {
        assertSamePath(this.existingFile, this.resolver.getPathFromSetting(this.sensorContext, "new path key", "existing.ts"));
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredButEmptyAndFallbackExists() {
        this.sensorContext.settings().setProperty("new path key", "");
        assertSamePath(this.existingFile, this.resolver.getPathFromSetting(this.sensorContext, "new path key", "existing.ts"));
    }
    
    @Test
    public void returnsNull_ifPrimaryNotConfiguredAndFallbackNull() {
        Optional<String> pathFromSetting = this.resolver.getPathFromSetting(this.sensorContext, "new path key", null);
        assertFalse(pathFromSetting.isPresent());
    }
    
    @Test
    public void returnsNull_ifRequestedPathDoesNotExist() {
        this.sensorContext.settings().setProperty("new path key", "missing.ts");
        Optional<String> pathFromSetting = this.resolver.getPathFromSetting(this.sensorContext, "new path key", "notexisting.ts");
        assertFalse(pathFromSetting.isPresent());
    }


    @Test
    public void returnsAbsolutePathToFile_ifAlreadyAbsoluteAndExists() {
        this.sensorContext.settings().setProperty("new path key", this.existingFile.getAbsolutePath());
        assertSamePath(this.existingFile, this.resolver.getPathFromSetting(this.sensorContext, "new path key", "not me"));
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
