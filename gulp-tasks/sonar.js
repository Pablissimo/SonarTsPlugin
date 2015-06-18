var gulp = require('gulp');
var gutil = require('gulp-util');
var sonar = require('gulp-sonar');
var path = require('path');
var cfg = require('../config');
var sourceMap = require('source-map');
var fs = require('fs');
var readline = require('readline');



gulp.task('sonar:analyze', ['sonar:tscoverage'], function () {
    var options = {
        sonar: {
            debug: true,
            host: {
                url: "sonar.url"
            },
            jdbc: {
                url: "sonar.db.url",
                username: "db.user",
                password: "db.password"
            },
            projectKey: "project.key",
            projectName: "project.name",
            projectVersion: '1.0.0',
            // comma-delimited string of source directories
            sources: "ts.files.root.folder",
            language: 'ts',
            sourceEncoding: 'UTF-8'
        }
    };

    // gulp source doesn't matter, all files are referenced in options object above
    return gulp.src('thisFileDoesNotExist.js', { read: false })
        .pipe(sonar(options));
});


function checkIfLineCoverageExists(coverageReport) {
    try {
        fs.openSync(coverageReport, "r");
        return true;
    } catch (err) {
        return false;
    }

    return false;
}

gulp.task('sonar:tscoverage', function () {
    var jsCoverageReportName = "karmacoverage.report.folder" + "/lcov.info";
    var lineCoverageExists = checkIfLineCoverageExists(jsCoverageReportName);

    var tsCoverageReportName = "karmacoverage.report.folder" + "/tslcov.info";
    if (lineCoverageExists) {
        var rd = readline.createInterface({
            input: fs.createReadStream(jsCoverageReportName),
            output: process.stdout,
            terminal: false
        });

        fs.openSync(tsCoverageReportName, "w");
        var currentFileName = "";
        var currentFileSourceMapConsumer = undefined;

        //Typescript Sonar Plugin currently handles only SF, DA, BRDA lines
        rd.on('line', function (lineObject) {
            var line = lineObject.toString();

            if (line.substring(0, 3).indexOf("SF") !== -1) {
                currentFileName = line.substring(line.indexOf(":") + 1);

                if (currentFileName !== "") {
                    var content = fs.readFileSync(currentFileName + ".map", {encoding: 'utf-8'});
                    currentFileSourceMapConsumer = new sourceMap.SourceMapConsumer(content);
                }

                if (currentFileSourceMapConsumer.sources && currentFileSourceMapConsumer.sources.length > 0) {
                    var sourceTsFile = getSourceTsFile(currentFileSourceMapConsumer);
                    fs.appendFileSync(tsCoverageReportName, "SF:" + sourceTsFile + "\n",
                        {encoding: 'utf-8'});
                } else {
                    fs.appendFileSync(tsCoverageReportName, line + "\n",
                        {encoding: 'utf-8'});
                }
            } else if (line.substring(0, 3).indexOf("DA") !== -1) {
                generateLine("DA", line, currentFileSourceMapConsumer, tsCoverageReportName);
            } else if (line.substring(0, 5).indexOf("BRDA") !== -1) {
                generateLine("BRDA", line, currentFileSourceMapConsumer, tsCoverageReportName);
            } else {
                fs.appendFileSync(tsCoverageReportName, line + "\n", {encoding: 'utf-8'});
            }
        }).on('error', function (e) {
            console.log("Unable to read coverage report");
        });
    } else {
        console.log("Line coverage report does not exists. Skipping test coverage report.");

        //delete the ts coverage as it can be an old one.
        try {
            fs.unlinkSync(tsCoverageReportName);
        } catch (err) {
            //do nothing
        }
    }
});

function getSourceTsFile(currentFileSourceMapConsumer) {
    return currentFileSourceMapConsumer.sources[0].replace("/src", "./app");
}


function generateLine(prefix, line, currentFileSourceMapConsumer, tsCoverageReportName) {
    var lineNumberArray = line.substring(line.indexOf(":") + 1).split(",");
    var lineNumber = parseInt(lineNumberArray[0]);

    var originalPosition = undefined;
    for (var i = 1; i < 150; i++) {
        originalPosition = currentFileSourceMapConsumer.originalPositionFor({line: lineNumber, column: i});

        if (originalPosition.line != null && originalPosition.line != "") {
            break;
        }
    }

    if (originalPosition && originalPosition.line !== "" && originalPosition.line !== null) {
        lineNumberArray[0] = originalPosition.line;
        fs.appendFileSync(tsCoverageReportName, prefix + ":" + lineNumberArray.join(",") + "\n", {encoding: 'utf-8'});
    }
}
