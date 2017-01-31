package io.github.sleroy.sonar;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.removeStart;

/**
 * Created by Administrator on 31.01.2017.
 * Utility class to generate some configuration informations
 */
public class CsvFileGeneratorTest {


    @Test
    public void test() {
        try (BufferedReader fr = new BufferedReader(new FileReader(new File("C:\\rules.csv")))) {
            List<String> lines = new ArrayList<>();
            List<String> keys = new ArrayList<>();
            String str;


            while ((str = fr.readLine()) != null) {
                System.out.println(str);
                String[] strings = str.split(";");
                String key = clean(strings[0]);
                String message = clean(strings[1]);
                lines.add(MessageFormat.format("{0}=true", key));
                lines.add(MessageFormat.format("{0}.name={1}", key, message));
                lines.add(MessageFormat.format("{0}.severity=MINOR", key));
                lines.add(MessageFormat.format("{0}.debtFunc=CONSTANT_ISSUE", key));
                lines.add(MessageFormat.format("{0}.debtScalar=1min", key));
                lines.add("");
                keys.add("\"" + key + "\"");

            }
            FileUtils.writeLines(new File("src\\main\\resources\\rules.properties"), lines);
            System.out.println(keys);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String clean(String string) {
        return StringUtils.removeEnd(
                removeStart(
                        StringUtils.replace(string, "�", " ")
                        , "\"")
                , "\"").trim();
    }

}
