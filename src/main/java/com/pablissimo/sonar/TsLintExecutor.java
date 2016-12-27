package com.pablissimo.sonar;

import java.util.List;

public interface TsLintExecutor {
    List<String> execute(TsLintExecutorConfig config, List<String> files);
}
