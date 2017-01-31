package io.github.sleroy.sonar.api;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.sensor.SensorContext;

@BatchSide
public interface PathResolver {
    String getPath(SensorContext context, String settingKey, String defaultValue);
}
