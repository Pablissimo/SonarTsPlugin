package com.pablissimo.sonar;

import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;

@BatchSide
@FunctionalInterface
public interface LOCSensor {
    Map<InputFile, Set<Integer>> execute(SensorContext ctx);
}
