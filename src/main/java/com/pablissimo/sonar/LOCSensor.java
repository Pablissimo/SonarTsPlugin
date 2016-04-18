package com.pablissimo.sonar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;

public class LOCSensor implements Sensor {
    private static final Logger LOG = LoggerFactory.getLogger(LOCSensor.class);

    private FileSystem fileSystem;

    /**
     * Use of IoC to get Settings and FileSystem
     */
    public LOCSensor(FileSystem fs) {
        this.fileSystem = fs;
    }

    public boolean shouldExecuteOnProject(Project project) {
        // This sensor is executed only when there are TypeScript files
        return fileSystem.hasFiles(fileSystem.predicates().hasLanguage("ts"));
    }

    public void analyse(Project project, SensorContext sensorContext) {
        // This sensor count the Line of source code in every .ts file

        for (InputFile inputFile : fileSystem.inputFiles(fileSystem.predicates().hasLanguage(
                "ts"))) {
            int value = this.getNonCommentLineCount(inputFile);
            sensorContext.saveMeasure(inputFile, new Measure<Integer>(
                    CoreMetrics.NCLOC, (double) value));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    protected BufferedReader getReaderFromFile(InputFile inputFile) throws FileNotFoundException {
        return new BufferedReader(new FileReader(inputFile.file()));
    }

    private int getNonCommentLineCount(InputFile inputFile) {
        int value = 0;
        BufferedReader br;
        try {
            br = this.getReaderFromFile(inputFile);

            boolean isEOF;
            boolean isCommentOpen = false;
            boolean isACommentLine;
            do {

                String line = br.readLine();
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
                    if ((!line.equals("")) && !isACommentLine) {
                        value++;
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
        return value;
    }
}
