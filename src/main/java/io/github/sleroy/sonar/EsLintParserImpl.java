package io.github.sleroy.sonar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.sleroy.sonar.api.EsLintParser;
import io.github.sleroy.sonar.model.EsLintFile;
import io.github.sleroy.sonar.model.EsLintIssue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.BatchSide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@BatchSide
public class EsLintParserImpl implements EsLintParser {
    private static final Logger LOG = LoggerFactory.getLogger(EsLintParserImpl.class);
    private static final Pattern REPLACE_PATTERN = Pattern.compile("\\]\\[");

    private static String getFixedUpOutput(String toParse) {
        if (toParse.contains("][")) {
            // Pre 4.0.0-versions of TsLint return nonsense for its JSON output
            // when faced with multiple files so we need to fix it up before we
            // do anything else
            return REPLACE_PATTERN.matcher(toParse).replaceAll(",");
        }

        return toParse;
    }

    @Override
    public Map<String, List<EsLintIssue>> parse(List<String> toParse) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();

        List<EsLintFile> allScannedFiles = new ArrayList<>(100);

        for (String batch : toParse) {
            EsLintFile[] batchIssues = gson.fromJson(getFixedUpOutput(batch), EsLintFile[].class);

            if (batchIssues == null) {
                continue;
            }
            allScannedFiles.addAll(Arrays.asList(batchIssues));
        }

        // Remap by filename
        Map<String, List<EsLintFile>> fileBag = allScannedFiles.stream()
                .collect(Collectors.groupingBy(EsLintFile::getFilePath));

        // Reduce all issues
        Map<String, List<EsLintIssue>> toIssues = fileBag.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                v -> v.getValue().stream()
                        .map(EsLintFile::getMessages)
                        .flatMap(List::stream)
                        .collect(Collectors.toList())
        ));


        return toIssues;
    }
}