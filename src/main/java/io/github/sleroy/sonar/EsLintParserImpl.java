package io.github.sleroy.sonar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sleroy.sonar.api.EsLintParser;
import io.github.sleroy.sonar.model.EsLintIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BatchSide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@BatchSide
public class EsLintParserImpl implements EsLintParser {
    private static final Logger LOG = LoggerFactory.getLogger(EsLintParserImpl.class);
    
    public Map<String, List<EsLintIssue>> parse(List<String> toParse) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        
        List<EsLintIssue> allIssues = new ArrayList<EsLintIssue>();
        
        for (String batch : toParse) {
            EsLintIssue[] batchIssues = gson.fromJson(getFixedUpOutput(batch), EsLintIssue[].class);
            
            if (batchIssues == null) {
                continue;
            }
            
            for (EsLintIssue batchIssue : batchIssues) {
                allIssues.add(batchIssue);
            }
        }

        // Remap by filename
        Map<String, List<EsLintIssue>> toReturn = new HashMap<String, List<EsLintIssue>>();
        for (EsLintIssue issue : allIssues) {
            List<EsLintIssue> issuesByFile = toReturn.get(issue.getName());
            if (issuesByFile == null) {
                issuesByFile = new ArrayList<EsLintIssue>();
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