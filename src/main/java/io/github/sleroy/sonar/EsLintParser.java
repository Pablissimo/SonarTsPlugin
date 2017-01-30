package io.github.sleroy.sonar;

import java.util.List;
import java.util.Map;

import io.github.sleroy.sonar.model.EsLintIssue;

public interface EsLintParser {
    Map<String, List<EsLintIssue>> parse(List<String> rawOutputBatches);
}
