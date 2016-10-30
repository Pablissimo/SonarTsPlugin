package com.pablissimo.sonar;

import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class CombinedCoverageSensor implements Sensor {

    protected LOCSensor getLOCSensor() {
        return new LOCSensorImpl();
    }
    
    protected TsCoverageSensor getCoverageSensor() {
        return new TsCoverageSensorImpl();
    }
    
    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Combined LCOV and LOC sensor");
        descriptor.onlyOnLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        // First - LOC everything up, as we'll need LOC for uncovered lines metrics
        Map<InputFile, Set<Integer>> nonCommentLinesByFile = getLOCSensor().execute(context);

        // Now the LCOV pass can properly handle files that don't appear in 
        // configuration and set lines-to-cover values as required
        getCoverageSensor().execute(context, nonCommentLinesByFile);
    }
    
}
