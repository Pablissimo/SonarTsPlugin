package io.github.sleroy.sonar.api;

import io.github.sleroy.sonar.EsLintExecutorConfig;
import org.sonar.api.batch.BatchSide;

import java.util.List;

@BatchSide
public interface EsLintExecutor {
    List<String> execute(EsLintExecutorConfig config, List<String> files);
}
