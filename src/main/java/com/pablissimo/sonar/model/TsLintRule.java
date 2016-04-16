package com.pablissimo.sonar.model;

public class TsLintRule {
    public final String key;
    public final String name;
    public final String severity;
    public final String html_description;

    public TsLintRule(String key, String severity, String name, String html_description) {
        this.key = key;
        this.severity = severity;
        this.name = name;
        this.html_description = html_description;
    }
}
