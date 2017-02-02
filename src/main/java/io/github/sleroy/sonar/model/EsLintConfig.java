package io.github.sleroy.sonar.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines the EsLint configuration. It takes basically a list of rules to be executed.
 */
public class EsLintConfig {
    private final Map<String, Object> rules;

    public EsLintConfig() {
        this.rules = new HashMap<>(100);
    }

    public Map<String, Object> getRules() {
        return this.rules;
    }

    public void addEnabledRule(String name) {
        this.rules.put(name, true);
    }

    public void addDisabledRule(String name) {
        this.rules.put(name, false);
    }

    public void addRuleWithArgs(String name, Object... args) {
        this.rules.put(name, args);
    }
}
