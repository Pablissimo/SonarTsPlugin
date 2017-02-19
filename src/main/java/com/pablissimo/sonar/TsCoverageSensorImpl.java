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
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TsCoverageSensorImpl implements TsCoverageSensor {

    private static final Logger LOG = LoggerFactory.getLogger(TsCoverageSensorImpl.class);

    private void saveZeroValueForAllFiles(SensorContext context, Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile) {
        for (InputFile inputFile : context.fileSystem().inputFiles(context.fileSystem().predicates().hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
          saveZeroValue(inputFile, context, nonCommentLineNumbersByFile.get(inputFile));
        }
    }
    
    private void saveZeroValue(InputFile inputFile, SensorContext context, Set<Integer> nonCommentLineNumbers) {
          NewCoverage newCoverage = 
                  context
                  .newCoverage()
                  .ofType(CoverageType.UNIT)
                  .onFile(inputFile);
    
          if (nonCommentLineNumbers != null) {
              for (Integer nonCommentLineNumber : nonCommentLineNumbers) {
                  newCoverage.lineHits(nonCommentLineNumber, 0);
              }
          }
          else {
              for (int i = 1; i <= inputFile.lines(); i++) {
                  newCoverage.lineHits(i, 0);
              }
          }

          newCoverage.save();
    }
    
    protected void saveMeasureFromLCOVFile(SensorContext context, Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile) {
        String providedPath = context.settings().getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH);
        File lcovFile = getIOFile(context.fileSystem().baseDir(), providedPath);

        if (!lcovFile.isFile()) {
            LOG.warn("No coverage information will be saved because LCOV file cannot be analysed. Provided LCOV file path: {}", providedPath);
            return;
        }

        LOG.info("Analysing {}", lcovFile);

        LCOVParser parser = getParser(context, new File[]{ lcovFile });
        Map<InputFile, NewCoverage> coveredFiles = parser.parseFile(lcovFile);
        
        final boolean ignoreNotFound = isIgnoreNotFoundActivated(context);
        
        for (InputFile file : context.fileSystem().inputFiles(context.fileSystem().predicates().hasLanguage(TypeScriptLanguage.LANGUAGE_KEY))) {
            try {
                NewCoverage fileCoverage = coveredFiles.get(file);
                
                if (fileCoverage != null) {
                    fileCoverage.save();
                }
                else if (!ignoreNotFound) {
                    // colour all lines as not executed
                    LOG.debug("Default value of zero will be saved for file: {}", file.relativePath());
                    LOG.debug("Because was not present in LCOV report.");
                    saveZeroValue(file, context, nonCommentLineNumbersByFile.get(file));
                }
            } catch (Exception e) {
                LOG.error("Problem while calculating coverage for " + file.absolutePath(), e);
            }
        }
    }

    protected LCOVParser getParser(SensorContext context, File[] files) {
        return LCOVParserImpl.create(context, files);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private boolean isForceZeroCoverageActivated(SensorContext ctx) {
        return ctx.settings().getBoolean(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE);
    }

    private boolean isIgnoreNotFoundActivated(SensorContext ctx) {
        return ctx.settings().getBoolean(TypeScriptPlugin.SETTING_IGNORE_NOT_FOUND);
    }

    private boolean isLCOVReportProvided(SensorContext ctx) {
        return StringUtils.isNotBlank(ctx.settings().getString(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH));
    }

    public File getIOFile(File baseDir, String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(baseDir, path);
        }

        return file;
    }

    @Override
    public void execute(SensorContext ctx, Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile) {
        Map<InputFile, Set<Integer>> nonCommentLineMap = nonCommentLineNumbersByFile;
        
        if (nonCommentLineMap == null) {
            nonCommentLineMap = new HashMap<>();
        }
        
        if (isLCOVReportProvided(ctx)) {
            saveMeasureFromLCOVFile(ctx, nonCommentLineMap);
        } else if (isForceZeroCoverageActivated(ctx)) {
            saveZeroValueForAllFiles(ctx, nonCommentLineMap);
        }
    }
}
