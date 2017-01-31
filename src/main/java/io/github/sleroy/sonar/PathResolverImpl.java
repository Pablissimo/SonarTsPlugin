package io.github.sleroy.sonar;

import io.github.sleroy.sonar.api.PathResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;

import java.io.File;

public class PathResolverImpl implements PathResolver {
    private static final Logger LOG = LoggerFactory.getLogger(EsLintExecutorImpl.class);

    @Override
    public String getPath(SensorContext context, String settingKey, String defaultValue) {
        // Prefer the specified path
        String toReturn = context.settings().getString(settingKey);

        // Fall back to a file system search if null or doesn't exist
        if (toReturn == null || toReturn.isEmpty()) {
            LOG.debug("Path " + settingKey + " not specified, falling back to " + defaultValue);
            toReturn = defaultValue;
        } else {
            LOG.debug("Found " + settingKey + " Lint path to be '" + toReturn + "'");
        }

        String esLintAbsolutePath = getAbsolutePath(context, toReturn);
        LOG.info("EsLint Absolute path is {}", esLintAbsolutePath);
        return esLintAbsolutePath;
    }

    /**
     * Computes the absolute path of a resource from a string obtained from Sonar Properties
     *
     * @param context  the sensor context
     * @param toReturn the default value
     * @return the absolute resource path or null if the resource does not exist.
     */
    String getAbsolutePath(SensorContext context, String toReturn) {
        if (toReturn != null) {
            File candidateFile = new java.io.File(toReturn);
            LOG.info("#1 Trying to resolve EsLint in {}", candidateFile);
            if (!candidateFile.isAbsolute()) {
                candidateFile = new java.io.File(context.fileSystem().baseDir().getAbsolutePath(), toReturn);
                LOG.info("#2 Trying to resolve EsLint in {}", candidateFile);
            }

            if (!doesFileExist(candidateFile)) {
                return null;
            }

            return candidateFile.getAbsolutePath();
        }

        return null;
    }

    /**
     * Tests if a path is existing.
     *
     * @param f
     * @return
     */
    boolean doesFileExist(File f) {
        return f.exists();
    }
}
