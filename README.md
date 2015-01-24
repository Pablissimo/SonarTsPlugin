SonarTsPlugin
=============

SonarQube plugin for TypeScript files

##Overview

This is a **not even alpha-level yet** SonarQube plugin for analysing projects with TypeScript content that supports:
* TsLint for code quality information
* Importing LCOV files for unit test coverage information

It's unfinished in the following respects:
* Plug-in code quality needs improved
* No unit test coverage of the plugin
* Exceptionally little error handling
* No overall code metrics like LOC, NCLOC

It's presented only for the interested, and the brave.

##Requirements
* Java 1.7+
* SonarQube 4.4+ (may or may not work with others)

The plugin has so far *only been tested on Windows* and it'll be no surprise if it fails on Linux just now.

##Building
* Download the source
* Build with maven, *mvn install*

##Installation
* Install Node.js
* Install TsLint with *npm install -g tslint*
* Find the path to TsLint and copy it - will be similar to *C:\Users\\[Username]\AppData\Roaming\npm\node_modules\tslint\bin\tslint* on Windows
* Copy .jar file from target/ after build to SonarQube extensions folder
* Restart SonarQube server
* Browse to SonarQube web interface, login as Admin, hit up Settings
* Find the TypeScript tab, paste in the TsLint path
* Hit the Rules tab, then the TsLint rule set, then apply it to your project - configure rules as required
* If LCOV data available, add *sonar.ts.lcov.reportpath=lcov.dat* to your sonar-project.properties file (replace lcov.dat with your lcov output, will be sought relative to the sonar-project.properties file)
* Run sonar-runner
* TsLint rule breaches should be shown in the web view

##Licence
MIT

##With thanks
The LCOV parser is directly copied from the community JavaScript SonarQube plug-in, which is LGPL'd.
