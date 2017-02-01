package io.github.sleroy.sonar.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the EsLint configuration. It takes basically a list of rules to be executed.
 */
public class EsLintConfig {
    private final Map<String, Object> rules;

    public EsLintConfig() {
        rules = new HashMap<>(100);
    }

    public Map<String, Object> getRules() {
        return rules;
    }

    public void addEnabledRule(String name) {
        rules.put(name, true);
    }

    public void addDisabledRule(String name) {
        rules.put(name, false);
    }

    public void addRule(String name, Object... args) {
        rules.put(name, args);
    }
}
