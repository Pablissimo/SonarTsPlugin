package com.pablissimo.sonar;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.coverage.NewCoverage;

public interface LCOVParser {
    Map<InputFile, NewCoverage> parseFile(File file);
    Map<InputFile, NewCoverage> parse(List<String> lines);
}
