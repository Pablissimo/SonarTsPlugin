package com.pablissimo.sonar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.measures.CoreMetrics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CommentSensorImpl implements CommentSensor {
    private static final Logger LOG = LoggerFactory.getLogger(CommentSensorImpl.class);

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private BufferedReader getReaderFromFile(InputFile inputFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(inputFile.file()));
    }

    private Set<Integer> getCommentLineNumbers(InputFile inputFile) {
        HashSet<Integer> toReturn = new HashSet<>();

        int lineNumber = 0;

        BufferedReader br;
        try {
            br = this.getReaderFromFile(inputFile);

            boolean isEOF;
            boolean isCommentOpen = false;
            boolean isACommentLine;
            do {
                String line = br.readLine();
                lineNumber++;

                if (line != null) {
                    isACommentLine = isCommentOpen;
                    line = line.trim();

                    if (isCommentOpen) {
                        if (line.contains("*/")) {
                            isCommentOpen = false;
                            isACommentLine = false;
                        } else if (line.equals("*")) {
                            isACommentLine = false;
                        }
                    } else {
                        if (line.startsWith("/*")) {
                            if (line.contains("*/")) {
                                isCommentOpen = false;
                            } else {
                                isCommentOpen = true;
                            }
                            if (line.equals("/*") || line.endsWith("/*")) {
                                isACommentLine = false;
                            } else {
                                isACommentLine = true;
                            }
                        } else if (line.startsWith("*")) {
                            if (line.equals("*")) {
                                isACommentLine = false;
                            } else {
                                isACommentLine = true;
                            }
                        } else if (line.startsWith("*/")) {
                            isCommentOpen = false;
                            isACommentLine = false;
                        } else if (line.contains("//") && !line.equals("//")) {
                            isACommentLine = true;
                        } else if (line.contains("/*")) {
                            if (line.contains("*/")) {
                                isCommentOpen = false;

                                if (line.substring(line.indexOf("/*") + 1, line.indexOf("*/") - 1).trim().length() > 0) {
                                    isACommentLine = true;
                                }
                            } else {
                                isCommentOpen = true;
                            }
                        }


//                        if (line.contains("/*")) {
//                            if (line.contains("*/")) {
//                                isCommentOpen = false;
//                            } else {
//                                isCommentOpen = true;
//                            }
//                            isACommentLine = true;
//                        }
                    }
                    isEOF = true;
                    line = line.replaceAll("\\n|\\t|\\s", "");
                    if (isACommentLine) {
                        toReturn.add(lineNumber);
                    }
                } else {
                    isEOF = false;
                }
            } while (isEOF);

            br.close();

        } catch (FileNotFoundException e) {
            LOG.error("File not found", e);
        } catch (IOException e) {
            LOG.error("Error while reading BufferedReader", e);
        }

        return toReturn;
    }

    @Override
    public Map<InputFile, Set<Integer>> execute(SensorContext ctx) {
        HashMap<InputFile, Set<Integer>> toReturn = new HashMap<>();

        Iterable<InputFile> affectedFiles =
            ctx
                .fileSystem()
                .inputFiles(ctx.fileSystem().predicates().hasLanguage(TypeScriptLanguage.LANGUAGE_KEY));

        for (InputFile inputFile : affectedFiles) {
            Set<Integer> commentLineNumbers = this.getCommentLineNumbers(inputFile);
            toReturn.put(inputFile, commentLineNumbers);

            ctx.<Integer>newMeasure().forMetric(CoreMetrics.COMMENT_LINES).on(inputFile).withValue(commentLineNumbers.size()).save();
        }

        return toReturn;
    }
}
