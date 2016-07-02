package com.pablissimo.sonar.model;

public class TsLintRule {
    public final String key;
    public final String name;
    public final String severity;
    public final String htmlDescription;

    public TsLintRule(String key, String severity, String name, String htmlDescription) {
        this.key = key;
        this.severity = severity;
        this.name = name;
        this.htmlDescription = htmlDescription;
    }
}
