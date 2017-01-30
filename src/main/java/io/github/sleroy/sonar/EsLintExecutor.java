package io.github.sleroy.sonar;

import java.util.List;

import org.sonar.api.batch.BatchSide;

@BatchSide
public interface EsLintExecutor {
    List<String> execute(EsLintExecutorConfig config, List<String> files);
}
