package com.pablissimo.sonar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sonar.api.batch.BatchSide;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pablissimo.sonar.model.TsLintIssue;

@BatchSide
public class TsLintParserImpl implements TsLintParser {
    @Override
    public Map<String, List<TsLintIssue>> parse(List<String> toParse) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        
        List<TsLintIssue> allIssues = new ArrayList<>();
        
        for (String batch : toParse) {
            TsLintIssue[] batchIssues = gson.fromJson(getFixedUpOutput(batch), TsLintIssue[].class);
            
            if (batchIssues == null) {
                continue;
            }
            
            for (TsLintIssue batchIssue : batchIssues) {
                allIssues.add(batchIssue);
            }
        }

        // Remap by filename
        Map<String, List<TsLintIssue>> toReturn = new HashMap<>();
        for (TsLintIssue issue : allIssues) {
            List<TsLintIssue> issuesByFile = toReturn.get(issue.getName());
            if (issuesByFile == null) {
                issuesByFile = new ArrayList<>();
                toReturn.put(issue.getName(), issuesByFile);
            }
            
            issuesByFile.add(issue);
        }

        return toReturn;
    }
    
    private String getFixedUpOutput(String toParse) {
        if (toParse.contains("][")) {
            // Pre 4.0.0-versions of TsLint return nonsense for its JSON output 
            // when faced with multiple files so we need to fix it up before we 
            // do anything else
            return toParse.replaceAll("\\]\\[", ",");
        }
        
        return toParse;
    }
}