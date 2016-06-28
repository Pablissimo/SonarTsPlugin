package com.pablissimo.sonar;

import com.pablissimo.sonar.model.TsLintIssue;

public interface TsLintParser {
    TsLintIssue[][] parse(String toParse);
}
