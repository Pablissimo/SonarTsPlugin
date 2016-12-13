package com.pablissimo.sonar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.pablissimo.sonar.model.TsLintIssue;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TsLintParserImpl implements TsLintParser {

    public Map<String, List<TsLintIssue>> parse(List<String> toParse) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        List<TsLintIssue> allIssues = toParse.stream()
            .map(batch -> gson.fromJson(getFixedUpOutput(batch), TsLintIssue[].class))
            .filter(Objects::nonNull)
            .flatMap(Arrays::stream)
            .collect(Collectors.toList());

        // Remap by filename
        return allIssues.stream()
            .collect(Collectors.groupingBy(TsLintIssue::getName));
    }

    private String getFixedUpOutput(String toParse) {
        if (StringUtils.contains(toParse, ("]["))) {
            // Pre 4.0.0-versions of TsLint return nonsense for its JSON output
            // when faced with multiple files so we need to fix it up before we
            // do anything else
            return toParse.replaceAll("\\]\\[", ",");
        }

        return toParse;
    }
}
