package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;

public class LOCSensorTest {
    FileSystem fileSystem;
    FilePredicates filePredicates;
    FilePredicate predicate;

    List<File> files;
    File file;
    
    LOCSensor sensor;
    
    SensorContext sensorContext;
    
    @Before
    public void setUp() throws Exception {
        this.file = mock(File.class);
        doReturn(true).when(this.file).isFile();

        this.files = new ArrayList<File>(Arrays.asList(new File[] {
            this.file
        }));
        
        ArrayList<InputFile> inputFiles = new ArrayList<InputFile>();
        inputFiles.add(mock(InputFile.class));

        this.fileSystem = mock(FileSystem.class);
        this.predicate = mock(FilePredicate.class);
        when(fileSystem.files(this.predicate)).thenReturn(this.files);
        when(fileSystem.inputFiles(this.predicate)).thenReturn(inputFiles);

        this.sensorContext = mock(SensorContext.class);
                
        this.filePredicates = mock(FilePredicates.class);
        when(this.fileSystem.predicates()).thenReturn(this.filePredicates);
        when(filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION)).thenReturn(this.predicate);
        when(fileSystem.hasFiles(this.predicate)).thenReturn(true);
                
        this.sensor = spy(new LOCSensor(this.fileSystem));
    }    

    @Test
    public void shouldExecuteOnProject_ReturnsTrue_WhenAnyTsFiles() {
        assertTrue(this.sensor.shouldExecuteOnProject(null));
    }

    @Test
    public void shouldExecuteOnProject_ReturnsFalse_WhenNoTsFiles() {
        when(fileSystem.hasFiles(this.predicate)).thenReturn(false);
        assertFalse(this.sensor.shouldExecuteOnProject(null));
    }
    
    @Test
    public void getStringReturnsClassName() {
        assertEquals("LOCSensor", new LOCSensor(this.fileSystem).toString());
    }
    
    @Test
    public void basicBlockCommentsDiscounted() throws FileNotFoundException {
        assertLineCount("blockcomments1", 2);
    }
    
    @Test
    public void blockCommentsNotConfusedWithNestedComments() throws FileNotFoundException {
        assertLineCount("blockcomments2", 2);
    }
    
    @Test
    public void linesEndingWithABlockCommentStillCounted() throws FileNotFoundException {
        assertLineCount("blockcomments3", 1);
    }
    
    @Test
    public void oneLineBlockCommentsDoNotConfuseCounting() throws FileNotFoundException {
        assertLineCount("blockcomments4", 1);
    }
    
    @Test
    public void oneLineBlockCommentAtEndOfRealLineShouldNotConsiderNextLinesAsComments() throws FileNotFoundException {
        assertLineCount("blockcomments5", 2);
    }
    
    @Test
    public void lineLevelCommentsAndWhitespaceHandledCorrectly() throws FileNotFoundException {
        assertLineCount("linecomments", 2);
    }
    
    private void assertLineCount(String testName, int expected) throws FileNotFoundException {
        ClassLoader loader = getClass().getClassLoader();
        URL resource = loader.getResource("loc/" + testName + ".txt");
        InputStream stream = loader.getResourceAsStream("loc/" + testName + ".txt");
        this.file = new File(resource.getFile());
        
        doReturn(new BufferedReader(new InputStreamReader(stream))).when(this.sensor).getReaderFromFile(any(InputFile.class));

        final List<Measure<Integer>> capturedMeasures = new ArrayList<Measure<Integer>>();
        Answer<Void> captureMeasure = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                capturedMeasures.add((Measure<Integer>) invocation.getArguments()[1]);
                return null;
            }
        };

        when(this.sensorContext.saveMeasure(any(InputFile.class), any(Measure.class))).then(captureMeasure);

        this.sensor.analyse(null, this.sensorContext);
        
        assertEquals(1, capturedMeasures.size());
        assertEquals(CoreMetrics.NCLOC, capturedMeasures.get(0).getMetric());
        assertEquals(expected, (int) capturedMeasures.get(0).getIntValue());
    }
}
