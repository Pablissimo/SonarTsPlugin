package io.github.sleroy.sonar;

import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

@BatchSide
public interface EsCoverageSensor {
    public abstract void execute(SensorContext ctx, Map<InputFile, Set<Integer>> nonCommentLineNumbersByFile);
}