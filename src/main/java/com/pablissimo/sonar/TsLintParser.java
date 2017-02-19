package com.pablissimo.sonar;

import java.util.List;
import java.util.Map;

import com.pablissimo.sonar.model.TsLintIssue;

@FunctionalInterface
public interface TsLintParser {
    Map<String, List<TsLintIssue>> parse(List<String> rawOutputBatches);
}
