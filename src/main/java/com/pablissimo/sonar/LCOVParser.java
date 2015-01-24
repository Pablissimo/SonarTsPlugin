package com.pablissimo.sonar;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.sonar.api.measures.CoverageMeasuresBuilder;

public interface LCOVParser {
	Map<String, CoverageMeasuresBuilder> parseFile(File file);
	Map<String, CoverageMeasuresBuilder> parse(List<String> lines);
}
