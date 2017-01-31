package io.github.sleroy.sonar.api;

import io.github.sleroy.sonar.model.EsLintIssue;

import java.util.List;
import java.util.Map;

public interface EsLintParser {
    Map<String, List<EsLintIssue>> parse(List<String> rawOutputBatches);
}
