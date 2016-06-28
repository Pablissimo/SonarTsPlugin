package com.pablissimo.sonar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pablissimo.sonar.model.TsLintIssue;

public class TsLintParserImpl implements TsLintParser {
    public TsLintIssue[][] parse(String toParse) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        return gson.fromJson(toParse, TsLintIssue[][].class);
    }
}