/*
 * Pretty heavily modified version of the existing SonarQube LCOV parser implementation
 * 
 * SonarQube JavaScript Plugin
 * Copyright (C) 2011-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.pablissimo.sonar;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.CheckForNull;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.coverage.CoverageType;
import org.sonar.api.batch.sensor.coverage.NewCoverage;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * http://ltp.sourceforge.net/coverage/lcov/geninfo.1.php
 */
public class LCOVParserImpl implements LCOVParser {

  private static final String SF = "SF:";
  private static final String DA = "DA:";
  private static final String BRDA = "BRDA:";

  private final Map<InputFile, NewCoverage> coverageByFile;
  private final SensorContext context;
  private final List<String> unresolvedPaths = new ArrayList<>();

  private static final Logger LOG = Loggers.get(LCOVParser.class);

  private LCOVParserImpl(List<String> lines, SensorContext context) {
    this.context = context;
    this.coverageByFile = parse(lines);
  }

  static LCOVParser create(SensorContext context, File... files) {
    final List<String> lines = new LinkedList<>();
    for(File file: files) {
      try {
        lines.addAll(Files.lines(file.toPath()).collect(Collectors.<String>toList()));
      } catch (IOException e) {
        throw new IllegalArgumentException("Could not read content from file: " + file, e);
      }
    }
    
    return new LCOVParserImpl(lines, context);
  }

  public Map<InputFile, NewCoverage> coverageByFile() {
    return coverageByFile;
  }

  List<String> unresolvedPaths() {
    return unresolvedPaths;
  }
  
  @Override
  public Map<InputFile, NewCoverage> parseFile(File file) {
      final List<String> lines;
      try 
      {
          lines = Files.readAllLines(file.toPath());
      } 
      catch (IOException e) 
      {
          throw new IllegalArgumentException("Could not read content from file: " + file, e);
      }
      
      return parse(lines);
  }

  @Override
  public Map<InputFile, NewCoverage> parse(List<String> lines) {
    final Map<InputFile, FileData> files = new HashMap<>();
    FileData fileData = null;

    for (String line : lines) {
      if (line.startsWith(SF)) {
        // SF:<absolute path to the source file>
        fileData = loadCurrentFileData(files, line);
      } else if (fileData != null) {
        if (line.startsWith(DA)) {
          // DA:<line number>,<execution count>[,<checksum>]
          String execution = line.substring(DA.length());
          String executionCount = execution.substring(execution.indexOf(',') + 1);
          String lineNumber = execution.substring(0, execution.indexOf(','));

          try {
            fileData.addLine(Integer.valueOf(lineNumber), Integer.valueOf(executionCount));
          } catch (IllegalArgumentException e) {
            logWrongDataWarning("DA", lineNumber, e);
          }
        } else if (line.startsWith(BRDA)) {
          // BRDA:<line number>,<block number>,<branch number>,<taken>
          String[] tokens = line.substring(BRDA.length()).trim().split(",");
          String lineNumber = tokens[0];
          String branchNumber = tokens[1] + tokens[2];
          String taken = tokens[3];

          try {
            fileData.addBranch(Integer.valueOf(lineNumber), branchNumber, "-".equals(taken) ? 0 : Integer.valueOf(taken));
          } catch (IllegalArgumentException e) {
            logWrongDataWarning("BRDA", lineNumber, e);
          }
        }
      }

    }

    Map<InputFile, NewCoverage> coveredFiles = new HashMap<>();
    for (Map.Entry<InputFile, FileData> e : files.entrySet()) {
      NewCoverage newCoverage = context.newCoverage().onFile(e.getKey()).ofType(CoverageType.UNIT);
      e.getValue().save(newCoverage);
      coveredFiles.put(e.getKey(), newCoverage);
    }
    return coveredFiles;
  }

  private static void logWrongDataWarning(String dataType, String lineNumber, IllegalArgumentException e) {
    LOG.warn(String.format("Problem during processing LCOV report: can't save %s data for line %s (%s).", dataType, lineNumber, e.getMessage()));
  }

  @CheckForNull
  private FileData loadCurrentFileData(final Map<InputFile, FileData> files, String line) {
    String filePath = line.substring(SF.length());
    FileData fileData = null;
    
    // some tools (like Istanbul, Karma) provide relative paths, so let's consider them relative to project directory
    InputFile inputFile = null;
    try {
        Paths.get(filePath);
        inputFile = context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(filePath));    
    }
    catch (InvalidPathException ex) {
        LOG.warn("LCOV file referred to path that appears invalid (not just not on disk): " + filePath, ex);
    }   
        
    // Try to accommodate Angular projects that, when the angular template loader's used
    // by checking for a ! in the filepath if the path isn't found - have a bash at seeking
    // everything after the last ! as a second fallback pass
    if (inputFile == null && filePath.contains("!") && (filePath.lastIndexOf('!') + 1) < filePath.length()) {
        String amendedPath = filePath.substring(filePath.lastIndexOf('!') + 1);
        
        LOG.debug("Failed to resolve " + filePath + " as a valid source file, so attempting " + amendedPath + " instead");
        
        inputFile = context.fileSystem().inputFile(context.fileSystem().predicates().hasPath(amendedPath));
    }
    
    if (inputFile != null) {
      fileData = files.get(inputFile);
      if (fileData == null) {
        fileData = new FileData(inputFile);
        files.put(inputFile, fileData);
      }
    } else {
      LOG.debug("Failed to resolve path " + filePath + " to a file in the analysis set");
      unresolvedPaths.add(filePath);
    }
    return fileData;
  }

  private static class FileData {
    /**
     * line number -> branch number -> taken
     */
    private Map<Integer, Map<String, Integer>> branches = new HashMap<>();

    /**
     * line number -> execution count
     */
    private Map<Integer, Integer> hits = new HashMap<>();

    /**
     * Number of lines in the file
     * Required to check if line exist in a file, see {@link #checkLine(Integer)}
     */
    private final int linesInFile;

    private final String filename;
    private static final String WRONG_LINE_EXCEPTION_MESSAGE = "Line with number %s doesn't belong to file %s";

    FileData(InputFile inputFile) {
      linesInFile = inputFile.lines();
      filename = inputFile.relativePath();
    }

    void addBranch(Integer lineNumber, String branchNumber, Integer taken) {
      checkLine(lineNumber);

      Map<String, Integer> branchesForLine = branches.get(lineNumber);
      if (branchesForLine == null) {
        branchesForLine = new HashMap<String, Integer>();
        branches.put(lineNumber, branchesForLine);
      }
      Integer currentValue = branchesForLine.get(branchNumber);
      branchesForLine.put(branchNumber, (currentValue == null ? 0 : currentValue) + taken);
    }

    void addLine(Integer lineNumber, Integer executionCount) {
      checkLine(lineNumber);

      Integer currentValue = hits.get(lineNumber);
      if (currentValue == null) {
          currentValue = 0;
      }
      
      hits.put(lineNumber, currentValue + executionCount);
    }

    void save(NewCoverage newCoverage) {
      for (Map.Entry<Integer, Integer> e : hits.entrySet()) {
        newCoverage.lineHits(e.getKey(), e.getValue());
      }
      for (Map.Entry<Integer, Map<String, Integer>> e : branches.entrySet()) {
        int conditions = e.getValue().size();
        int covered = 0;
        for (Integer taken : e.getValue().values()) {
          if (taken > 0) {
            covered++;
          }
        }

        newCoverage.conditions(e.getKey(), conditions, covered);
      }
    }

    private void checkLine(Integer lineNumber) {
      if (lineNumber < 1 || lineNumber > linesInFile) {
        throw new IllegalArgumentException(String.format(WRONG_LINE_EXCEPTION_MESSAGE, lineNumber, filename));
      }
    }

  }
}
