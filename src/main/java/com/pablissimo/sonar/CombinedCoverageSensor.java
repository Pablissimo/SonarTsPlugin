package com.pablissimo.sonar;

import java.util.Map;
import java.util.Set;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;

public class CombinedCoverageSensor implements Sensor {
    private LOCSensor locSensor;
    private TsCoverageSensor coverageSensor;
    
    public CombinedCoverageSensor(LOCSensor locSensor, TsCoverageSensor coverageSensor) {
        this.locSensor = locSensor;
        this.coverageSensor = coverageSensor;
    }
    
    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Combined LCOV and LOC sensor");
        descriptor.onlyOnLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        // First - LOC everything up, as we'll need LOC for uncovered lines metrics
        Map<InputFile, Set<Integer>> nonCommentLinesByFile = this.locSensor.execute(context);

        // Now the LCOV pass can properly handle files that don't appear in 
        // configuration and set lines-to-cover values as required
        this.coverageSensor.execute(context, nonCommentLinesByFile);
    }
    
}
