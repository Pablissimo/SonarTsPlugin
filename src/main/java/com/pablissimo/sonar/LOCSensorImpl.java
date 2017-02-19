package com.pablissimo.sonar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;

public class LOCSensorImpl implements LOCSensor {
    private static final Logger LOG = LoggerFactory.getLogger(LOCSensorImpl.class);
    
    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private BufferedReader getReaderFromFile(InputFile inputFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(inputFile.file()));
    }

    private Set<Integer> getNonCommentLineNumbers(InputFile inputFile) {
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
                            isACommentLine = true;
                        }
                    } else {
                        if (line.startsWith("//")) {
                            isACommentLine = true;
                        }
                        if (line.startsWith("/*")) {
                            if (line.contains("*/")) {
                                isCommentOpen = false;
                            } else {
                                isCommentOpen = true;
                            }
                            isACommentLine = true;

                        } else if (line.contains("/*")) {
                            if (line.contains("*/")) {
                                isCommentOpen = false;
                            } else {
                                isCommentOpen = true;
                            }
                            isACommentLine = false;
                        }
                    }
                    isEOF = true;
                    line = line.replaceAll("\\n|\\t|\\s", "");
                    if (!("".equals(line) || isACommentLine)) {
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
            Set<Integer> nonCommentLineNumbers = this.getNonCommentLineNumbers(inputFile);
            toReturn.put(inputFile, nonCommentLineNumbers);
            
            ctx.<Integer>newMeasure().forMetric(CoreMetrics.NCLOC).on(inputFile).withValue(nonCommentLineNumbers.size()).save();
        }
        
        return toReturn;
    }
}
