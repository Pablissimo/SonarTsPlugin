package com.pablissimo.sonar;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;

public class TypeScriptPluginTest {
    TypeScriptPlugin plugin;

    @Before
    public void setUp() throws Exception {
        this.plugin = new TypeScriptPlugin();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void advertisesAppropriateExtensions() {
        List extensions = this.plugin.getExtensions();

        assertEquals(6, extensions.size());
		assertTrue(extensions.contains(TypeScriptRuleProfile.class));
		assertTrue(extensions.contains(TypeScriptLanguage.class));
		assertTrue(extensions.contains(TsLintSensor.class));
		assertTrue(extensions.contains(TsCoverageSensor.class));
		assertTrue(extensions.contains(TsRulesDefinition.class));
    }

    @Test
    public void decoratedWithPropertiesAnnotation() {
        Annotation[] annotations = plugin.getClass().getAnnotations();

        assertEquals(1, annotations.length);
        assertEquals(Properties.class, annotations[0].annotationType());
    }

    @Test
    public void definesExpectedProperties() {
        Annotation annotation = plugin.getClass().getAnnotations()[0];
        Properties propertiesAnnotation = (Properties) annotation;

        assertEquals(5, propertiesAnnotation.value().length);

        Property[] properties = propertiesAnnotation.value();
        assertNotNull(findPropertyByName(properties, TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES));
        assertNotNull(findPropertyByName(properties, TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE));
        assertNotNull(findPropertyByName(properties, TypeScriptPlugin.SETTING_LCOV_REPORT_PATH));
        assertNotNull(findPropertyByName(properties, TypeScriptPlugin.SETTING_TS_LINT_PATH));
        assertNotNull(findPropertyByName(properties, TypeScriptPlugin.SETTING_TS_LINT_CONFIG_PATH));
    }

    @Test
    public void tsLintPathSetting_definedAppropriately() {
        Property property = findPropertyByName(TypeScriptPlugin.SETTING_TS_LINT_PATH);

        assertEquals(PropertyType.STRING, property.type());
        assertEquals("", property.defaultValue());
        assertEquals(false, property.project());
        assertEquals(true, property.global());
    }

    @Test
    public void excludeTypeDefinitionFilesSetting_definedAppropriately() {
        Property property = findPropertyByName(TypeScriptPlugin.SETTING_EXCLUDE_TYPE_DEFINITION_FILES);

        assertEquals(PropertyType.BOOLEAN, property.type());
        assertEquals("true", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(false, property.global());
    }

    @Test
    public void lcovReportPathSetting_definedAppropriately() {
        Property property = findPropertyByName(TypeScriptPlugin.SETTING_LCOV_REPORT_PATH);

        assertEquals(PropertyType.STRING, property.type());
        assertEquals("", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(false, property.global());
    }

    @Test
    public void forceZeroCoverageSetting_definedAppropriately() {
        Property property = findPropertyByName(TypeScriptPlugin.SETTING_FORCE_ZERO_COVERAGE);

        assertEquals(PropertyType.BOOLEAN, property.type());
        assertEquals("false", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(false, property.global());
    }

    private Property findPropertyByName(String property) {
        return findPropertyByName(((Properties) plugin.getClass().getAnnotations()[0]).value(), property);
    }

    private static Property findPropertyByName(Property[] properties, final String name) {
        return (Property) CollectionUtils.find(Arrays.asList(properties), new Predicate() {
            @Override
            public boolean evaluate(Object arg0) {
                return ((Property) arg0).key().equals(name);
            }
        });
    }
}
