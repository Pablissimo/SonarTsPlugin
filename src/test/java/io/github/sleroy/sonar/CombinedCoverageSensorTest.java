package io.github.sleroy.sonar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.internal.DefaultSensorDescriptor;
import org.sonar.api.batch.sensor.internal.SensorContextTester;

public class CombinedCoverageSensorTest {
    LOCSensor locSensor;
    EsCoverageSensor coverageSensor;
    
    SensorContextTester sensorContext;
    
    CombinedCoverageSensor sensor;
    
    @Before
    public void setUp() throws Exception {
        this.sensorContext = SensorContextTester.create(new File(""));
        this.locSensor = mock(LOCSensor.class);
        this.coverageSensor = mock(EsCoverageSensor.class);
        
        this.sensor = new CombinedCoverageSensor(this.locSensor, this.coverageSensor);
    }
    
    @Test
    public void describe_setsName() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        
        this.sensor.describe(desc);
        
        assertEquals(desc.name(), "Combined LCOV and LOC sensor");
    }
    
    @Test
    public void describe_setsLanguage() {
        DefaultSensorDescriptor desc = new DefaultSensorDescriptor();
        
        this.sensor.describe(desc);
        
        assertEquals(EsLintLanguage.LANGUAGE_KEY, desc.languages().iterator().next());
    }
    
    @Test
    public void execute_callsLocSensor() {
        this.sensor.execute(this.sensorContext);
        
        verify(this.locSensor, times(1)).execute(eq(this.sensorContext));
    }
    
    @Test
    public void execute_callsCoverageSensorWithLocSensorOutput() {
        Map<InputFile, Set<Integer>> locOutput = new HashMap<InputFile, Set<Integer>>();
        
        when(this.locSensor.execute(eq(this.sensorContext))).thenReturn(locOutput);
        
        this.sensor.execute(this.sensorContext);
        
        verify(this.coverageSensor, times(1)).execute(eq(this.sensorContext), eq(locOutput)); 
    }
}
