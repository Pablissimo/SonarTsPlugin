package io.github.sleroy.sonar;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Predicate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;
import org.sonar.api.PropertyType;
import org.sonar.api.SonarQubeVersion;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class EsLintPluginTest {
    public static final int EXPECTED_PROPERTIES = 6;
    EsLintPlugin plugin;

    private static Property findPropertyByName(Property[] properties,
                                               final String name) {

        return CollectionUtils.find(Arrays.asList(properties),
                new Predicate() {
                    @Override
                    public boolean evaluate(Object arg0) {
                        return ((Property) arg0).key().equals(name);
                    }
                });
    }

    @Before
    public void setUp() throws Exception {
        this.plugin = new EsLintPlugin();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void advertisesAppropriateExtensions() {
        Plugin.Context context = new Plugin.Context(SonarQubeVersion.V5_6);

        this.plugin.define(context);

        List extensions = context.getExtensions();

        assertTrue(extensions.contains(EsLintRuleProfile.class));
        assertTrue(extensions.contains(EsLintLanguage.class));
        assertTrue(extensions.contains(EsLintSensor.class));
        assertTrue(extensions.contains(EsRulesDefinition.class));
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

        assertEquals(EXPECTED_PROPERTIES, propertiesAnnotation.value().length);

        Property[] properties = propertiesAnnotation.value();

        assertNotNull(findPropertyByName(properties,
            EsLintPlugin.SETTING_ES_LINT_ENABLED));
        assertNotNull(findPropertyByName(properties,
                EsLintPlugin.SETTING_ES_LINT_PATH));
        assertNotNull(findPropertyByName(properties,
                EsLintPlugin.SETTING_ES_LINT_CONFIG_PATH));
        assertNotNull(findPropertyByName(properties,
                EsLintPlugin.SETTING_ES_LINT_TIMEOUT));
        assertNotNull(findPropertyByName(properties,
                EsLintPlugin.SETTING_ES_LINT_RULES_DIR));
        assertNotNull(findPropertyByName(properties,
                EsLintPlugin.SETTING_ES_RULE_CONFIGS));
    }

    @Test
    public void tsLintPathSetting_definedAppropriately() {
        Property property = findPropertyByName(EsLintPlugin.SETTING_ES_LINT_PATH);

        assertEquals(PropertyType.STRING, property.type());
        assertEquals("", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(true, property.global());
    }

    @Test
    public void tsLintTimeoutSettings_definedAppropriately() {
        Property property = findPropertyByName(EsLintPlugin.SETTING_ES_LINT_TIMEOUT);

        assertEquals(PropertyType.INTEGER, property.type());
        assertEquals("60000", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(false, property.global());
    }

    @Test
    public void rulesDirSetting_definedAppropriately() {
        Property property = findPropertyByName(EsLintPlugin.SETTING_ES_LINT_RULES_DIR);

        assertEquals(PropertyType.STRING, property.type());
        assertEquals("", property.defaultValue());
        assertEquals(true, property.project());
        assertEquals(false, property.global());
    }

    @Test
    public void ruleConfigsSetting_definedAppropriately() {
        Property property = findPropertyByName(EsLintPlugin.SETTING_ES_RULE_CONFIGS);

        assertEquals(PropertyType.STRING, property.type());
        assertEquals("", property.defaultValue());
        assertEquals(false, property.project());
        assertEquals(true, property.global());
        assertEquals(2, property.fields().length);

        // name
        assertEquals("name", property.fields()[0].key());
        assertEquals(PropertyType.STRING, property.fields()[0].type());

        // config
        assertEquals("config", property.fields()[1].key());
        assertEquals(PropertyType.TEXT, property.fields()[1].type());
        assertEquals(120, property.fields()[1].indicativeSize());
    }

    private Property findPropertyByName(String property) {
        return findPropertyByName(((Properties) plugin.getClass()
                .getAnnotations()[0]).value(), property);
    }
}
