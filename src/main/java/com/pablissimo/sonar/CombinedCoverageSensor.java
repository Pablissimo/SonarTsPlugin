package com.pablissimo.sonar;

import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.measures.CoreMetrics;

import java.util.Map;
import java.util.Set;

public class CombinedCoverageSensor implements Sensor {
    private LOCSensor locSensor;
    private CommentSensor commentSensor;
    private TsCoverageSensor coverageSensor;

    public CombinedCoverageSensor(LOCSensor locSensor, CommentSensor commentSensor, TsCoverageSensor coverageSensor) {
        this.locSensor = locSensor;
        this.commentSensor = commentSensor;
        this.coverageSensor = coverageSensor;
    }

    @Override
    public void describe(SensorDescriptor descriptor) {
        descriptor.name("Combined LCOV, LOC and comments");
        descriptor.onlyOnLanguage(TypeScriptLanguage.LANGUAGE_KEY);
    }

    @Override
    public void execute(SensorContext context) {
        // First - LOC everything up, as we'll need LOC for uncovered lines metrics
        Map<InputFile, Set<Integer>> nonCommentLinesByFile = this.locSensor.execute(context);

        // Now the LCOV pass can properly handle files that don't appear in
        // configuration and set lines-to-cover values as required
        this.coverageSensor.execute(context, nonCommentLinesByFile);

        // Last but not least - get those comments and compute comment density
        Map<InputFile, Set<Integer>> commentedLinesByFile = this.commentSensor.execute(context);

        for (InputFile inputFile : nonCommentLinesByFile.keySet()) {
            Double commentDensity = 0.0;
            Double commentLines = new Double(commentedLinesByFile.get(inputFile).size());
            Double nonCommentLines = new Double(nonCommentLinesByFile.get(inputFile).size());
            if (commentedLinesByFile.containsKey(inputFile)) {
                commentDensity =  commentLines / (commentLines + nonCommentLines) * 100;
            }
            context.<Double>newMeasure().forMetric(CoreMetrics.COMMENT_LINES_DENSITY).on(inputFile).withValue(commentDensity).save();

        }

    }

}
