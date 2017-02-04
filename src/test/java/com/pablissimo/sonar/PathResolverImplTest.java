package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

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
                    .setLanguage(TypeScriptLanguage.LANGUAGE_KEY);
        
        this.sensorContext.fileSystem().add(file);
        
        this.resolver = new PathResolverImpl();
    }
    
    @Test
    public void returnsAbsolutePathToFile_ifSpecifiedAndExists() {
        String result = this.resolver.getPath(this.sensorContext, "path key", "not me");
        assertSamePath(this.existingFile, result);
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredAndFallbackExists() {
        String result = this.resolver.getPath(this.sensorContext, "new path key", "existing.ts");
        assertSamePath(this.existingFile, result);
    }
    
    @Test
    public void returnsAbsolutePathToFallbackFile_ifPrimaryNotConfiguredButEmptyAndFallbackExists() {
        this.sensorContext.settings().setProperty("new path key",  "");
        String result = this.resolver.getPath(this.sensorContext, "new path key", "existing.ts");
        assertSamePath(this.existingFile, result);
    }
    
    @Test
    public void returnsNull_ifPrimaryNotConfiguredAndFallbackNull() {
        String result = this.resolver.getPath(this.sensorContext, "new path key", null);
        assertNull(result);
    }
    
    @Test
    public void returnsNull_ifRequestedPathDoesNotExist() {
        this.sensorContext.settings().setProperty("new path key",  "missing.ts");
        String result = this.resolver.getPath(this.sensorContext, "new path key", "existing.ts");
        assertNull(result);
    }
    
    @Test
    public void returnsAbsolutePathToFile_ifAlreadyAbsoluteAndExists() {
        this.sensorContext.settings().setProperty("new path key", this.existingFile.getAbsolutePath());
        String result = this.resolver.getPath(this.sensorContext, "new path key", "not me");
        assertSamePath(this.existingFile, result);
    }

    /**
     * Asserts that the provided path is the same as the argument. Paths are converted in File objects to avoid case-sensitive issues on some filesystems.
     * @param existingFile the path to test against
     * @param argument the argument
     */
    private void assertSamePath(File existingFile, String argument) {
        if (argument == null)
            assertEquals(existingFile, argument);
        else {
            assertEquals(existingFile, new File(argument));
        }
    }
}