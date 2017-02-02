package io.github.sleroy.sonar.api;

import org.sonar.api.batch.BatchSide;
import org.sonar.api.batch.sensor.SensorContext;

import java.util.Optional;


@BatchSide
public interface PathResolver {
    Optional<String> getPathFromSetting(SensorContext context, String settingKey, String defaultValue);

    /**
     * Matches if the property returns a valid path. If the path is invalid, it does not provide a default value rather an Optional answer.
     *
     * @param context    the sensor context
     * @param settingKey the setting key
     * @return the absolute path provided by the setting or a missing value.
     */
    Optional<String> getPathFromSetting(SensorContext context, String settingKey);


    /**
     * Computes the absolute path of a resource from a string obtained from Sonar Properties
     *
     * @param context the sensor context
     * @param path    the path to check
     * @return the absolute resource path or null if the resource does not exist.
     */
    Optional<String> getAbsolutePath(SensorContext context, String path);
}
