package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TsCoverageSensorTest {
	TsCoverageSensor sensor;
	Settings settings;
	FilePredicates filePredicates;
	FileSystem fileSystem;
	SensorContext context;
	List<File> files;
	org.sonar.api.resources.File sonarFile;
	FilePredicate predicate;
	
	@Before
	public void setUp() throws Exception {
		this.files = new ArrayList<File>(Arrays.asList(new File[] {
			mock(File.class)
		}));
		
		this.fileSystem = mock(FileSystem.class);
		this.predicate = mock(FilePredicate.class);
		when(fileSystem.files(this.predicate)).thenReturn(this.files);	
		
		this.settings = mock(Settings.class);
		when(this.settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("lcovpath");
		
		this.filePredicates = mock(FilePredicates.class);
		when(this.fileSystem.predicates()).thenReturn(this.filePredicates);
		when(filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION)).thenReturn(this.predicate);
		
		this.sonarFile = mock(org.sonar.api.resources.File.class);
		
		this.sensor = spy(new TsCoverageSensor(fileSystem, settings));
		doReturn(this.sonarFile).when(this.sensor).fileFromIoFile(any(java.io.File.class), any(Project.class));
		this.context = mock(SensorContext.class);
	}

	@Test
	public void shouldExecuteOnProject_ReturnsTrue_WhenAnyTsFiles() {
		assertTrue(this.sensor.shouldExecuteOnProject(null));
	}
	
	@Test
	public void shouldExecuteOnProject_ReturnsFalse_WhenNoTsFiles() {
		when(fileSystem.files(this.predicate)).thenReturn(new ArrayList<File>());
		assertFalse(this.sensor.shouldExecuteOnProject(null));
	}
	
	@Test
	public void savesZeroValues_IfNoReportAndSettingEnabled() {
		when(this.settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("");
		when(this.settings.getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE)).thenReturn(true);
		
		Measure linesMeasure = mock(Measure.class);		
		when(this.context.getMeasure(eq(this.sonarFile), eq(CoreMetrics.LINES))).thenReturn(linesMeasure);
		
		Measure nclocLines = mock(Measure.class);
		when(nclocLines.getIntValue()).thenReturn(5);
		when(nclocLines.getValue()).thenReturn(5.0);
		when(this.context.getMeasure(eq(this.sonarFile), eq(CoreMetrics.NCLOC))).thenReturn(nclocLines);
		
		this.sensor.analyse(mock(Project.class), this.context);
		verify(context).saveMeasure(eq(this.sonarFile), any(Measure.class));
		verify(context).saveMeasure(eq(this.sonarFile), eq(CoreMetrics.LINES_TO_COVER), eq(5.0));
		verify(context).saveMeasure(eq(this.sonarFile), eq(CoreMetrics.UNCOVERED_LINES), eq(5.0));
	}
	
	@Test
	public void savesNothing_IfNoReportAndSettingDisabled() {
		when(this.settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH)).thenReturn("");
		when(this.settings.getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE)).thenReturn(false);
		this.sensor.analyse(mock(Project.class), this.context);
		verify(context, never()).saveMeasure(any(Resource.class), any(Measure.class));
		verify(context, never()).saveMeasure(any(Resource.class), any(Metric.class), any(Double.class));
	}
}
