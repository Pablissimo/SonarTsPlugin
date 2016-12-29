package com.pablissimo.sonar;

import static org.junit.Assert.*;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.coverage.internal.DefaultCoverage;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

public class LCOVParserImplTest {
    LOCSensor sensor;
    
    SensorContextTester sensorContext;
    DefaultInputFile inputFile;
    
    @Before
    public void setUp() throws Exception {
        this.sensorContext = SensorContextTester.create(new File(""));
                
        this.inputFile = 
                new DefaultInputFile("", "path/to/file.ts")
                    .setLanguage(TypeScriptLanguage.LANGUAGE_KEY)
                    .setLines(3);
        
        this.sensorContext.fileSystem().add(this.inputFile);
    }
    
    @Test
    public void parsesBasicLcovFiles() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("basic");
        DefaultCoverage c = (DefaultCoverage) coverage.get(this.inputFile);
        
        assertEquals((Integer) 3, c.hitsByLine().get(1));
        assertEquals((Integer) 0, c.hitsByLine().get(2));
        assertEquals((Integer) 1, c.hitsByLine().get(3));
        
        assertEquals(3, c.linesToCover());
    }
    
    @Test
    public void parsesAngularTemplateLoaderOutput() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("angular");
        DefaultCoverage c = (DefaultCoverage) coverage.get(this.inputFile);
        
        assertEquals((Integer) 3, c.hitsByLine().get(1));
        assertEquals((Integer) 0, c.hitsByLine().get(2));
        assertEquals((Integer) 1, c.hitsByLine().get(3));
        
        assertEquals(3, c.linesToCover());
    }
    
    @Test
    public void handlesNoContent() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("blank");
       
        assertNotNull(coverage);
        assertEquals(0, coverage.size());
    }
    
    @Test
    public void handlesNoLineHitsForASingleFile() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("nolinehits");
        DefaultCoverage c = (DefaultCoverage) coverage.get(this.inputFile);
        
        assertEquals(1, coverage.size());
        
        assertNotNull(c);
        assertNull(c.hitsByLine().get(1));
        assertNull(c.hitsByLine().get(2));
        assertNull(c.hitsByLine().get(3));
    }
    
    @Test
    public void ignoresFilesNotPartOfAnalysisSet() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("existingandnot");
        DefaultCoverage c = (DefaultCoverage) coverage.get(this.inputFile);
        
        assertNotNull(c);
        assertEquals(1, coverage.size());
    }
    
    @Test
    public void handlesFilesEndingWithExclamationMarkIfNotPartOfSet() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("angularendswithbang");
        
        assertNotNull(coverage);
        assertEquals(0, coverage.size());
    }
    
    @Test
    public void handlesOutOfRangeLineNumbers() {
        Map<InputFile, NewCoverage> coverage = executeForTestCase("outofrangelines");
        DefaultCoverage c = (DefaultCoverage) coverage.get(this.inputFile);

        assertNotNull(c);
        assertEquals(1, coverage.size());
        
        assertEquals((Integer) 3, c.hitsByLine().get(1));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void createThrowsWhenFileDoesNotExist() {
        File nonExistent = new File("whatever");
        LCOVParserImpl.create(this.sensorContext, nonExistent);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void parseFileThrowsWhenFileDoesNotExist() {
        File nonExistent = new File("whatever");
        LCOVParser parser = getParser(resource("basic"));
        
        parser.parseFile(nonExistent);
    }
    
    private Map<InputFile, NewCoverage> executeForTestCase(String testName) {
        File lcovFile = resource(testName);
        LCOVParser parser = getParser(lcovFile);
        
        return parser.parseFile(lcovFile);
    }
    
    private LCOVParser getParser(File lcovFile) {
        return LCOVParserImpl.create(this.sensorContext, lcovFile);
    }
    
    private File resource(String testName) {        
        URL lcovUrl = LCOVParserImplTest.class.getClassLoader().getResource("./lcov/" + testName + ".lcov");
        
        try {
            File lcovFile = new File(lcovUrl.toURI());
            return lcovFile;
        } 
        catch (URISyntaxException e) {
            return null;
        }
    }
}
