package com.pablissimo.sonar.model;

import java.util.HashMap;
import java.util.Map;

public class TsLintConfig {
    private Map<String, Object> rules;

    public TsLintConfig() {
        this.rules = new HashMap<>();
    }

    public Map<String, Object> getRules() {
        return rules;
    }

    public void addRule(String name, boolean enabled) {
        rules.put(name, enabled);
    }

    public void addRule(String name, Object... args) {
        rules.put(name, args);
    }
}
