package com.pablissimo.sonar.model;

public class TsLintIssue {
	private String fileName;
    private TsLintPosition startPosition;
    private TsLintPosition endPosition;
    private String failure;
    private String ruleName;
    
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public TsLintPosition getStartPosition() {
		return startPosition;
	}
	
	public void setStartPosition(TsLintPosition startPosition) {
		this.startPosition = startPosition;
	}
	
	public TsLintPosition getEndPosition() {
		return endPosition;
	}
	
	public void setEndPosition(TsLintPosition endPosition) {
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