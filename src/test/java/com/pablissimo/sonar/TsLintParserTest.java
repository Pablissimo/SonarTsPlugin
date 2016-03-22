package com.pablissimo.sonar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.annotation.Annotation;

import org.junit.Test;
import org.sonar.api.Properties;
import org.sonar.api.Property;

import com.pablissimo.sonar.model.TsLintIssue;

public class TsLintParserTest {


    @Test
    public void parsesValidTsLintRecordIntoObject() {
        String toParse = "[{\"endPosition\":{\"character\":44,\"line\":23,\"position\":658},\"failure\":\"for statements must be braced\",\"name\":\"Tools.ts\",\"ruleName\":\"curly\",\"startPosition\":{\"character\":6,\"line\":22,\"position\":587}}]";
        TsLintIssue[] issues = new TsLintParserImpl().parse(toParse);

        assertEquals(1,  issues.length);

        TsLintIssue issue = issues[0];
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
}
