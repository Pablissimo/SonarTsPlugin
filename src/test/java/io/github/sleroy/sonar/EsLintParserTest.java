package io.github.sleroy.sonar;

import io.github.sleroy.sonar.model.EsLintIssue;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;


public class EsLintParserTest {
    /**
     * Tests the output of EsLint when no results are returned and the analysis is a success.
     */
    @Test
    public void eslint_successnoresults() {
        String parseRow1 = "[]";
        List<String> toParse = new ArrayList<String>();
        toParse.add(parseRow1);

        Map<String, List<EsLintIssue>> issues = new EsLintParserImpl().parse(toParse);

        assertEquals(0, issues.size());
    }

    /**
     * Tests the output of EsLint when the analysis is a success with results
     */
    @Test
    public void eslint_successWithResults() throws IOException {
        String parseRow1 = FileUtils.readFileToString(new File("src/test/resources/results/ok.json"));
        List<String> toParse = new ArrayList<String>();
        toParse.add(parseRow1);

        Map<String, List<EsLintIssue>> issues = new EsLintParserImpl().parse(toParse);

        assertEquals("Expected one file", 1, issues.size());

        assertEquals("Expected fifty-eight violations", 58, issues.get("c:\\workspace\\SonarTsPlugin\\src\\test\\resources\\dashboard.js").size());
    }

    /**
     * Tests the output of EsLint when the analysis has failed on a parsing error
     */
    @Test
    public void eslint_parsingFailure() throws IOException {
        String parseRow1 = FileUtils.readFileToString(new File("src/test/resources/results/parsingFailure.json"));
        List<String> toParse = new ArrayList<String>();
        toParse.add(parseRow1);

        Map<String, List<EsLintIssue>> issues = new EsLintParserImpl().parse(toParse);

        assertEquals("Expected one file", 1, issues.size());

        assertEquals("Expected one error, parsing error", 1, issues.get("c:\\workspace\\SonarTsPlugin\\src\\test\\resources\\angular.html").size());
    }


    @Test
    public void parseAGoodProjectWithNoIssues() {
        List<String> toParse = new ArrayList<String>();
        toParse.add("");
                
        Map<String, List<EsLintIssue>> issues = new EsLintParserImpl().parse(toParse);
        
        assertEquals(0, issues.size());        
    }
}
