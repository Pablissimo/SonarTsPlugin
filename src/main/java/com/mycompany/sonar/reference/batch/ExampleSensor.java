package com.mycompany.sonar.reference.batch;

import com.mycompany.sonar.reference.ExampleMetrics;
import com.mycompany.sonar.reference.ExamplePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

public class ExampleSensor implements Sensor {

  private static final Logger LOG = LoggerFactory.getLogger(ExampleSensor.class);

  private Settings settings;

  /**
   * Use of IoC to get Settings
   */
  public ExampleSensor(Settings settings) {
    this.settings = settings;
  }

  public boolean shouldExecuteOnProject(Project project) {
    // This sensor is executed on any type of projects
    return true;
  }

  public void analyse(Project project, SensorContext sensorContext) {
    String value = settings.getString(ExamplePlugin.MY_PROPERTY);
    LOG.info(ExamplePlugin.MY_PROPERTY + "=" + value);
    Measure measure = new Measure(ExampleMetrics.MESSAGE, value);
    sensorContext.saveMeasure(measure);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName();
  }

}
