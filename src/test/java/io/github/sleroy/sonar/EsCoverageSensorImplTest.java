package io.github.sleroy.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.batch.sensor.internal.SensorContextTester;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.internal.DefaultInputFile;
import org.sonar.api.config.Settings;

public class EsCoverageSensorImplTest {
    TsCoverageSensorImpl sensor;
    Settings settings;
    SensorContextTester context;
    LCOVParser parser;
    
    DefaultInputFile file;
    File lcovFile;
    
    @Before
    public void setUp() throws Exception {
        this.file = new DefaultInputFile("", "src/test/existing.ts").setLanguage(EsLintLanguage.LANGUAGE_KEY);
        this.file.setLines(5);
        
        this.settings = mock(Settings.class);
        when(this.settings.getString(EsLintPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("lcovpath");

        this.parser = mock(LCOVParser.class);

        this.sensor = Mockito.spy(new TsCoverageSensorImpl());
        this.context = SensorContextTester.create(new File(""));

        this.context.fileSystem().add(this.file);
        this.context.setSettings(this.settings);
        
        this.lcovFile = mock(File.class);
        when(this.lcovFile.isFile()).thenReturn(true);
        doReturn(this.lcovFile).when(this.sensor).getIOFile(any(File.class), eq("lcovpath"));
        doReturn(this.parser).when(this.sensor).getParser(eq(this.context), any(File[].class));
    }

    @Test
    public void savesZeroCoverage_IfNoReportAndSettingEnabled() {
        when(this.settings.getString(EsLintPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("");
        when(this.settings.getBoolean(EsLintPlugin.SETTING_FORCE_ZERO_COVERAGE)).thenReturn(true);

        this.sensor.execute(this.context, null);

        for (int i = 1; i <= this.file.lines(); i++) {
            assertEquals((Integer) 0, this.context.lineHits(this.file.key(), CoverageType.UNIT, i));
        }
    }

    @Test
    public void savesNothing_IfNoReportAndSettingDisabled() {
        when(this.settings.getString(EsLintPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("");
        when(this.settings.getBoolean(EsLintPlugin.SETTING_FORCE_ZERO_COVERAGE)).thenReturn(false);
        
        this.sensor.execute(this.context, null);

        for (int i = 1; i <= this.file.lines(); i++) {
            assertNull(this.context.lineHits(this.file.key(), CoverageType.UNIT, i));
        }
    }

    @Test
    public void doesNotCallParser_WhenNoLCOVPathSupplied() {
        when(this.settings.getString(EsLintPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("");
        
        this.sensor.execute(this.context, null);
        verify(this.parser, never()).parseFile(any(java.io.File.class));
    }

    @Test
    public void savesZeroCoverage_IfParserOutputsNothingForFile() {
        this.sensor.execute(this.context, null);

        for (int i = 1; i <= this.file.lines(); i++) {
            assertEquals((Integer) 0, this.context.lineHits(this.file.key(), CoverageType.UNIT, i));
        }
    }
    
    @Test
    public void usesNonCommentLinesSetForLinesToCoverMetrics_IfSettingZeroCoverage() {
        Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile = new HashMap<InputFile, Set<Integer>>();
        HashSet<Integer> nonCommentLineNumbers = new HashSet<Integer>();
        nonCommentLineNumbers.add(1);
        nonCommentLineNumbers.add(3);
        nonCommentLineNumbers.add(5);
        
        nonCommentLineNumbersByFile.put(this.file, nonCommentLineNumbers);
        
        this.sensor.execute(this.context, nonCommentLineNumbersByFile);

        // Expect lines 1, 3 and 5 to have zero coverage...
        assertEquals((Integer) 0, this.context.lineHits(this.file.key(), CoverageType.UNIT, 1));
        assertEquals((Integer) 0, this.context.lineHits(this.file.key(), CoverageType.UNIT, 3));
        assertEquals((Integer) 0, this.context.lineHits(this.file.key(), CoverageType.UNIT, 5));
     
        // and lines 2 and 5 to not have any coverage since they're not counted as code
        // according to our supplied map
        assertNull(this.context.lineHits(this.file.key(), CoverageType.UNIT, 2));
        assertNull(this.context.lineHits(this.file.key(), CoverageType.UNIT, 4));
    }

    @Test
    public void savesNoCoverage_IfNotFoundFilesAreIgnored() {   
        when(this.settings.getBoolean(EsLintPlugin.SETTING_IGNORE_NOT_FOUND)).thenReturn(true);
        this.sensor.execute(this.context, null);
        
        for (int i = 1; i <= this.file.lines(); i++) {
            assertNull(this.context.lineHits(this.file.key(), CoverageType.UNIT, i));
        }
    }

    @Test
    public void savesCoverage_IfParserOutputHasDetailsForFile() {
        HashMap<InputFile, NewCoverage> allFilesCoverage = new HashMap<InputFile, NewCoverage>();
        NewCoverage fileCoverage = spy(this.context.newCoverage());
        allFilesCoverage.put(this.file, fileCoverage);
        
        when(this.parser.parseFile(this.lcovFile)).thenReturn(allFilesCoverage);

        this.sensor.execute(this.context, null);

        verify(fileCoverage, times(1)).save();
    }
}
