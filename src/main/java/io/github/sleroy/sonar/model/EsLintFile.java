package io.github.sleroy.sonar.model;

import java.util.List;

/**
 * Defines the list of violations found by EsLint for a file.
 */
public class EsLintFile {

    private String filePath;

    private List<EsLintIssue> messages;

    private int errorCount;

    private int warningCount;

    private String source;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<EsLintIssue> getMessages() {
        return messages;
    }

    public void setMessages(List<EsLintIssue> messages) {
        //noinspection AssignmentToCollectionOrArrayFieldFromParameter
        this.messages = messages;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
