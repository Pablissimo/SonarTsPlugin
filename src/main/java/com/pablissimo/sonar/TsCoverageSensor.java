/*
 * Slightly modified version of SonarQube JavaScript Plugin
 * Copyright (C) 2011 SonarSource and Eriks Nukis
 * dev@sonar.codehaus.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package com.pablissimo.sonar;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FilePredicates;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.CoverageMeasuresBuilder;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.PropertiesBuilder;
import org.sonar.api.resources.Project;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TsCoverageSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(TsCoverageSensor.class);

  private final FileSystem moduleFileSystem;
  private final FilePredicates filePredicates;
  private final Settings settings;

  public TsCoverageSensor(FileSystem moduleFileSystem, Settings settings) {
    this.moduleFileSystem = moduleFileSystem;
    this.filePredicates = moduleFileSystem.predicates();
    this.settings = settings;
  }

  public boolean shouldExecuteOnProject(Project project) {
    return moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION)).iterator().hasNext();
  }

  public void analyse(Project project, SensorContext context) {
    if (isLCOVReportProvided()) {
      saveMeasureFromLCOVFile(project, context);

    } else if (isForceZeroCoverageActivated()) {
      saveZeroValueForAllFiles(project, context);
    }

    // Else, nothing to do, there will be no coverage information for JavaScript files.
  }

  protected void saveZeroValueForAllFiles(Project project, SensorContext context) {
    for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION))) {
      saveZeroValueForResource(this.fileFromIoFile(file, project), context);
    }
  }

  protected void saveMeasureFromLCOVFile(Project project, SensorContext context) {
    String providedPath = settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH);
    File lcovFile = getIOFile(moduleFileSystem.baseDir(), providedPath);

    if (!lcovFile.isFile()) {
      LOG.warn("No coverage information will be saved because LCOV file cannot be analysed. Provided LCOV file path: {}", providedPath);
      return;
    }

    LOG.info("Analysing {}", lcovFile);

    LCOVParser parser = getParser(moduleFileSystem.baseDir());
    Map<String, CoverageMeasuresBuilder> coveredFiles = parser.parseFile(lcovFile);

    for (File file : moduleFileSystem.files(this.filePredicates.hasLanguage(TypeScriptLanguage.LANGUAGE_EXTENSION))) {
      try {
    	 // LOG.info("File absolute path: {}", file.getAbsolutePath());
        CoverageMeasuresBuilder fileCoverage = coveredFiles.get(file.getAbsolutePath());
        //LOG.info("File coverage: {}", fileCoverage);
        org.sonar.api.resources.File resource = this.fileFromIoFile(file, project);
        //LOG.info("File Resource: {}", resource);
        
        if (fileCoverage != null) {
          for (Measure measure : fileCoverage.createMeasures()) {
            context.saveMeasure(resource, measure);
          }
        } else {
        	//LOG.info("Inside else {}", resource.getPath());
          // colour all lines as not executed
         // saveZeroValueForResource(resource, context);
        }
      } catch (Exception e) {
        LOG.error("Problem while calculating coverage for " + file.getAbsolutePath(), e);
      }
    }
  }
  
  protected org.sonar.api.resources.File fileFromIoFile(java.io.File file, Project project) {
	  return org.sonar.api.resources.File.fromIOFile(file, project);
  }
  
  protected LCOVParser getParser(File baseDirectory) {
	  return new LCOVParserImpl(baseDirectory);
  }
  
  private void saveZeroValueForResource(org.sonar.api.resources.File resource, SensorContext context) {
	    PropertiesBuilder<Integer, Integer> lineHitsData = new PropertiesBuilder<Integer, Integer>(CoreMetrics.COVERAGE_LINE_HITS_DATA);
	    
	    //String path = resource.getPath();
	    List<String> lines;
		try {
			lines = Files.readAllLines(Paths.get(resource.getPath()), Charset.defaultCharset());
		
	     
		    if (lines != null && lines.size() > 0) {
			    for (int x = 1; x < lines.size(); x++) {
			      lineHitsData.add(x, 0);
			    }
			
			    LOG.info("Number of lines: {}", lines.size());
			    // use non comment lines of code for coverage calculation
			    //Measure ncloc = context.getMeasure(resource, CoreMetrics.NCLOC);
			    context.saveMeasure(resource, lineHitsData.build());
			    //context.saveMeasure(resource, CoreMetrics.LINES_TO_COVER, ncloc.getValue());
			    //context.saveMeasure(resource, CoreMetrics.UNCOVERED_LINES, ncloc.getValue());
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

  private boolean isForceZeroCoverageActivated() {
	  return settings.getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE);
  }

  private boolean isLCOVReportProvided() {
	  return StringUtils.isNotBlank(settings.getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH));
  }

  /**
   * Returns a java.io.File for the given path.
   * If path is not absolute, returns a File with module base directory as parent path.
   */
  public File getIOFile(File baseDir, String path) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(baseDir, path);
    }

    return file;
  }
}