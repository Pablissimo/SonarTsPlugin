package io.github.sleroy.sonar.model;

/**
 * This class is the model for an issue as found by EsLint.
 */
public class EsLintIssue {
    private String name;
    private String message;
    private String ruleId;
    private int severity;
    private int column;
    private int line;
    private String source;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Returns the rule name.
     *
     * @return the rule name.
     */
    public String getRuleId() {
        return this.ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }


    public int getSeverity() {
        return this.severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public int getColumn() {
        return this.column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getLine() {
        return this.line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "EsLintIssue{" +
                "name='" + this.name + '\'' +
                ", message='" + this.message + '\'' +
                ", ruleId='" + this.ruleId + '\'' +
                ", severity=" + this.severity +
                ", column=" + this.column +
                ", line=" + this.line +
                ", source='" + this.source + '\'' +
                '}';
    }
}