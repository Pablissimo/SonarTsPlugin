package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;
import java.util.Optional;

public class PathResolverImpl implements PathResolver {
    private static final Logger LOG = LoggerFactory.getLogger(PathResolverImpl.class);

    /**
     * Tests if a path is existing.
     *
     * @param f the file
     * @return true if the file does exists
     */
    static boolean doesFileExist(File f) {
        return f.exists();
    }

    /**
     * Returns the path provided by the setting if valid.
     *
     * @param context    the sensor context
     * @param settingKey the setting key
     * @return the absolute path or nothing.
     */
    @Override
    public Optional<String> getPathFromSetting(SensorContext context, String settingKey) {
        // Prefer the specified path
        String propertyValue = context.settings().getString(settingKey);

        // Fall back to a file system search if null or doesn't exist
        if (propertyValue == null || propertyValue.isEmpty()) {
            PathResolverImpl.LOG.info("Path {} not specified", settingKey);
            return Optional.empty();
        } else {
            PathResolverImpl.LOG.info("Found {} Lint path to be '{}'", settingKey, propertyValue);
        }

        Optional<String> absolutePath = getAbsolutePath(context, propertyValue);
        PathResolverImpl.LOG.info("EsLint Absolute path is {}", absolutePath);
        return absolutePath;
    }

    @Override
    public Optional<String> getPathFromSetting(SensorContext context, String settingKey, String defaultValue) {
        Optional<String> path = getPathFromSetting(context, settingKey);

        // Fall back to a file system search if null or doesn't exist
        if (path.isPresent()) {
            return path;
        } else {
            PathResolverImpl.LOG.info("Path {} not specified, falling back to {}", settingKey, defaultValue);
            return this.getAbsolutePath(context, defaultValue);
        }
    }

    /**
     * Computes the absolute path of a resource from a string obtained from Sonar Properties
     *
     * @param context  the sensor context
     * @param path the path to check
     * @return the absolute resource path or null if the resource does not exist.
     */
    @Override
    public Optional<String> getAbsolutePath(SensorContext context, String path) {
        if (path != null) {
            File candidateFile = new File(path);
            PathResolverImpl.LOG.info("#1 Trying to resolve path in {}", candidateFile);
            if (!candidateFile.isAbsolute()) {
                candidateFile = new File(context.fileSystem().baseDir().getAbsolutePath(), path);
                PathResolverImpl.LOG.info("#2 Trying to resolve path in {}", candidateFile);
            }

            if (!doesFileExist(candidateFile)) {
                return Optional.empty();
            }

            return Optional.of(candidateFile.getAbsolutePath());
        }

        return Optional.empty();
    }
}
