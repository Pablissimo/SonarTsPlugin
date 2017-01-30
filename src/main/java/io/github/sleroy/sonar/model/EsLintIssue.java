package io.github.sleroy.sonar.model;

public class EsLintIssue {
    private String name;
    private EsLintPosition startPosition;
    private EsLintPosition endPosition;
    private String failure;
    private String ruleName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EsLintPosition getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(EsLintPosition startPosition) {
        this.startPosition = startPosition;
    }

    public EsLintPosition getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(EsLintPosition endPosition) {
        this.endPosition = endPosition;
    }

    public String getFailure() {
        return failure;
    }

    public void setFailure(String failure) {
        this.failure = failure;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}