package com.pablissimo.sonar;

import org.sonar.api.batch.sensor.SensorContext;

public interface PathResolver {
    String getPath(SensorContext context, String settingKey, String defaultValue);
}
