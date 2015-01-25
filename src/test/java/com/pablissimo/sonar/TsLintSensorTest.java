package com.pablissimo.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.FilePredicate;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.component.ResourcePerspectives;
import org.sonar.api.config.Settings;
import org.sonar.api.profiles.RulesProfile;

import edu.emory.mathcs.backport.java.util.Arrays;

public class TsLintSensorTest {
	Settings settings;
	FileSystem fileSystem;
	ResourcePerspectives perspectives;
	RulesProfile rulesProfile;
	FilePredicates filePredicates;
	FilePredicate predicate;
	
	List<File> files;
	File file;
	
	TsLintSensor sensor;
	
	@Before
	public void setUp() throws Exception {
		this.settings = mock(Settings.class);
		this.fileSystem = mock(FileSystem.class);
		this.perspectives = mock(ResourcePerspectives.class);
		this.rulesProfile = mock(RulesProfile.class);
		
		this.file = mock(File.class);
		doReturn(true).when(this.file).isFile();
		
		this.files = new ArrayList<File>(Arrays.asList(new File[] {
			this.file
		}));
		
		this.fileSystem = mock(FileSystem.class);
		this.predicate = mock(FilePredicate.class);
		when(fileSystem.files(this.predicate)).thenReturn(this.files);	
		
		this.filePredicates = mock(FilePredicates.class);
		when(this.fileSystem.predicates()).thenReturn(this.filePredicates);
		when(filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION)).thenReturn(this.predicate);
		
		this.sensor = new TsLintSensor(settings, fileSystem, perspectives, rulesProfile);
	}

	@After
	public void tearDown() throws Exception {
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
}
