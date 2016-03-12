package com.pablissimo.sonar;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.sonar.api.batch.Sensor;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.config.Settings;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.FileLinesContextFactory;
import org.sonar.api.measures.Measure;
import org.sonar.api.resources.Project;


public class LOCSensor implements Sensor {

    private Settings settings;
    private FileSystem fs;

    /**
     * Use of IoC to get Settings and FileSystem
     */
    public LOCSensor(FileLinesContextFactory _fileLinesContextFactory, Settings settings, FileSystem fs) {
        this.settings = settings;
        this.fs = fs;
    }

    public boolean shouldExecuteOnProject(Project project) {
        // This sensor is executed only when there are Typescript files
        return fs.hasFiles(fs.predicates().hasLanguage("ts"));
    }

    public void analyse(Project project, SensorContext sensorContext) {
        // This sensor count the Line of source code in every .ts file

        for (InputFile inputFile : fs.inputFiles(fs.predicates().hasLanguage("ts"))) {
            double value = this.getNumberCodeLine(inputFile);
            sensorContext.saveMeasure(inputFile, new Measure<Integer> (CoreMetrics.NCLOC, value));
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    private double getNumberCodeLine(InputFile inputFile){
        double value = 0;
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(inputFile.file()));

            boolean isEOF=false;
            boolean isCommentOpen = false;
            boolean isACommentLine = false;
            do{

                String line = br.readLine();
                if(line!=null){
                    isACommentLine = false;
                    line = line.trim();

                    if(isCommentOpen){
                        if(line.contains("*/")){
                            isCommentOpen = false;
                            isACommentLine = true;
                        }
                    }else{
                        if(line.startsWith("//")){
                            isACommentLine = true;
                        }
                        if(line.startsWith("/*")){
                            if(line.contains("*/")){
                                isCommentOpen = false;
                            }else{
                                isCommentOpen = true;
                            }
                            isACommentLine = true;

                        }else if(line.contains("/*")){
                            if(line.contains("*/")){
                                isCommentOpen = false;
                            }else{
                                isCommentOpen = true;
                            }
                            isACommentLine = false;
                        }
                    }
                    isEOF=true;
                    line=line.replaceAll("\\n|\\t|\\s", "");
                    if((!line.equals("")) && !isACommentLine) {
                        value ++;
                    }
                }else{
                    isEOF=false;
                }
            }while(isEOF);

            br.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return value;
    }
}
