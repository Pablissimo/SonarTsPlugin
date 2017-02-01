package io.github.sleroy.sonar.model;

/**
 * This class is the model for an issue as found by EsLint.
 */
public class EsLintIssue {
    private String name;
    private EsLintPosition startPosition;
    private EsLintPosition endPosition;
    private String failure;
    private String ruleName;
    private String fileName;

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

    /**
     * Returns the rule name.
     *
     * @return the rule name.
     */
    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}