package com.pablissimo.sonar;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.pablissimo.sonar.model.TsLintIssue;

public class TsLintParserTest {


    @Test
    public void parsesValidTsLintRecordIntoObject() {
        String parseRow1 = "[{\"endPosition\":{\"character\":44,\"line\":23,\"position\":658},\"failure\":\"for statements must be braced\",\"name\":\"Tools.ts\",\"ruleName\":\"curly\",\"startPosition\":{\"character\":6,\"line\":22,\"position\":587}}]";
        List<String> toParse = new ArrayList<String>();
        toParse.add(parseRow1);
        
        Map<String, List<TsLintIssue>> issues = new TsLintParserImpl().parse(toParse);

        assertEquals(1, issues.size());

        List<TsLintIssue> fileIssues = issues.get("Tools.ts");
        
        assertEquals(1, fileIssues.size());
        
        TsLintIssue issue = fileIssues.get(0);
        assertEquals(44, issue.getEndPosition().getCharacter());
        assertEquals(23, issue.getEndPosition().getLine());
        assertEquals(658, issue.getEndPosition().getPosition());

        assertEquals("for statements must be braced", issue.getFailure());
        assertEquals("Tools.ts", issue.getName());
        assertEquals("curly", issue.getRuleName());

        assertEquals(6, issue.getStartPosition().getCharacter());
        assertEquals(22, issue.getStartPosition().getLine());
        assertEquals(587, issue.getStartPosition().getPosition());
    }
    
    @Test
    public void parsesIssuesWithSameNameIntoSameBucket() {
        List<String> toParse = new ArrayList<String>();
        toParse.add("[{\"name\":\"Tools.ts\",\"ruleName\":\"tools1\"}]");
        toParse.add("[{\"name\":\"Tools.ts\",\"ruleName\":\"tools2\"}]");
        
        Map<String, List<TsLintIssue>> issues = new TsLintParserImpl().parse(toParse);
        
        assertEquals(1, issues.size());
        assertEquals(2, issues.get("Tools.ts").size());
    }
    
    @Test
    public void fixesUpBrokenBatchedOutputFromTsLintPriorTo_4_0_0() {
        List<String> toParse = new ArrayList<String>();
        toParse.add("[{\"name\":\"Tools.ts\",\"ruleName\":\"tools1\"}][{\"name\":\"Tools.ts\",\"ruleName\":\"tools2\"}]");
        
        Map<String, List<TsLintIssue>> issues = new TsLintParserImpl().parse(toParse);
        
        assertEquals(1, issues.size());
        assertEquals(2, issues.get("Tools.ts").size());
    }
    
    @Test
    public void parseAGoodProjectWithNoIssues() {
        List<String> toParse = new ArrayList<String>();
        toParse.add("");
                
        Map<String, List<TsLintIssue>> issues = new TsLintParserImpl().parse(toParse);
        
        assertEquals(0, issues.size());        
    }
}
