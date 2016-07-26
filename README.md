SonarTsPlugin
=============

SonarQube plugin for TypeScript files

[![Build Status](https://travis-ci.org/Pablissimo/SonarTsPlugin.svg?branch=master)](https://travis-ci.org/Pablissimo/SonarTsPlugin)
[![Coverage Status](https://coveralls.io/repos/Pablissimo/SonarTsPlugin/badge.svg?branch=master)](https://coveralls.io/r/Pablissimo/SonarTsPlugin?branch=master)

##Demos

A live deployed demo hitting a few large open-source TypeScript projects can be found here:
https://sonar.pablissimo.com

Suggestions for more projects (or ones with easy-to-gather code coverage info) appreciated!

* [Microsoft TypeScript Compiler](https://sonar.pablissimo.com/overview?id=18739)
* [Turbulenz Engine](https://sonar.pablissimo.com/overview?id=20352)
* [Microsoft Visual Studio Code](https://sonar.pablissimo.com/overview?id=19179)
* [Angular Framework](https://sonar.pablissimo.com/overview?id=18822)

##Overview

This is a **not even alpha-level yet** SonarQube plugin for analysing projects with TypeScript content that supports:
* TsLint for code quality information
* Importing LCOV files for unit test coverage information
* NCLOC metric generation

It's unfinished in the following respects:
* Plug-in code quality needs improved
* Incomplete unit test coverage of the plugin
* Exceptionally little error handling

It's presented only for the interested, and the brave.

###Breaking change in 0.2###
To more easily support changes to the rules TsLint understands, the plugin no longer generates a TsLint configuration file for you but instead you must now specify your own using the sonar.ts.tslintconfigpath configuration property (either in the web interface, or in your sonar-project.properties file).

##Requirements
* Java 1.7+
* SonarQube 4.4+ (may or may not work with others)
* TsLint 2.4.0+

The plugin has so far *only been tested on Windows* and it'll be no surprise if it fails on Linux just now.

##Building
* Download the source
* Build with maven, *mvn clean && mvn install*

##Installation
* Install Node.js
* Install TsLint (2.4.0+) with *npm install -g tslint*
* Find the path to TsLint and copy it - will be similar to *C:\Users\\[Username]\AppData\Roaming\npm\node_modules\tslint\bin\tslint* on Windows
* Copy .jar file from target/ after build to SonarQube extensions folder
* Restart SonarQube server
* Browse to SonarQube web interface, login as Admin, hit up Settings
* Find the TypeScript tab, paste in the TsLint path
* Hit the Rules tab, then the TsLint rule set, then apply it to your project - alter rule activation as required
* Add *sonar.ts.tslintconfigpath=tslint.json* to your sonar-project.properties file - change the path as required, relative to your properties file
* If LCOV data available, add *sonar.ts.lcov.reportpath=lcov.dat* to your sonar-project.properties file (replace lcov.dat with your lcov output, will be sought relative to the sonar-project.properties file)
* Run sonar-runner
* TsLint rule breaches should be shown in the web view

##Configuration

###Global configuration

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th></thead>
<tbody>
<tr><td>sonar.ts.tslintpath</td><td><b>Mandatory</b></td><td>Path to the installed copy of TsLint to use</td></tr>
<tr><td>sonar.ts.ruleconfigs</td><td><b>Optional</b></td><td>A list of configurations to map custom TsLint rules to dedicated SonarQube rules &amp; settings - see TsLint Custom Rules section below</td></tr>
</tbody>
</table>

###Project-level configuration

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th>
</thead>
<tbody>
<tr><td>sonar.ts.tslintconfigpath</td><td><b>Mandatory</b></td><td>Path to the tslint.json file that configures the rules to be used in linting</td></tr>
<tr><td>sonar.ts.excludetypedefinitionfiles</td><td><b>Optional</b></td><td>Excludes .d.ts files from analysis, defaults to true</td></tr>
<tr><td>sonar.ts.forceZeroCoverage</td><td><b>Optional</b></td><td>Forces code coverage percentage to zero when no report is supplied, defaults to false</td></tr>
<tr><td>sonar.ts.ignoreNotFound</td><td><b>Optional</b></td><td>Don't set code coverage percentage to zero when file is not found in report, defaults to false</td></tr>
<tr><td>sonar.ts.tslinttimeout</td><td><b>Optional</b></td><td>Max time to wait for TsLint to finish processing a single file (in milliseconds), defaults to 60 seconds</td></tr>
<tr><td>sonar.ts.tslintrulesdir</td><td><b>Optional</b></td><td>Path to a folder containing custom TsLint rules referenced in tslint.json</td></tr>
<tr><td>sonar.ts.lcov.reportpath</td><td><b>Optional</b></td><td>Path to an LCOV code-coverage report to be included in analysis</td></tr>
</tbody>
</table>

## TsLint Custom Rules

To present custom TsLint rules in SonarQube analysis, you can provide a configuration that maps the TsLint rules from your `sonar.ts.tslintrulesdir`
directory to dedicated Sonar rules for analysis.
The configuration for a TSLint Sonar rule consists of a line declaring the TSLint rule id, a boolean switch to enable or disable the rule if needed
and some attached properties that are used by Sonar for analysis and reporting.

For example taking the `export-name` rule from the [tslint-microsoft-contrib](https://github.com/Microsoft/tslint-microsoft-contrib) package,
a configuration for that rule in SonarTsPlugin could look as follows:

	export-name=true
	export-name.name=The name of the exported module must match the filename of the source file.
	export-name.severity=MAJOR
	export-name.description=This is case-sensitive but ignores file extension. Since version 1.0, this rule takes a list of regular expressions as a parameter. Any export name matching that regular expression will be ignored.
	export-name.debtFunc=LINEAR_OFFSET
	export-name.debtScalar=15min
	export-name.debtOffset=1h
	export-name.debtType=HARDWARE_RELATED_PORTABILITY

**You will need to restart the SonarQube server after configuring custom rules this way before subsequent analyses will pick them up. You will also need to activate the new rules after restart for any quality profile you want them to participate in - by default they will be disabled.**

* for documentation about the `technical debt` parameters look [here](http://docs.sonarqube.org/display/PLUG/Rule+Remediation+Costs) and [here](http://javadocs.sonarsource.org/5.2/apidocs/org/sonar/api/server/debt/DebtRemediationFunction.html)
* for possible values for `debtType` go [here](http://javadocs.sonarsource.org/5.2/apidocs/org/sonar/api/server/rule/RulesDefinition.SubCharacteristics.html)

##Licence
MIT

##Contributors
Thanks to the following for contributions to the plugin:
* [Alex Krauss](https://github.com/alexkrauss) and [Manuel Huber](https://github.com/nelo112) for work on improving compatibility with *nix OSes
* [schnee3](https://github.com/schnee3) for giving us some NCLOC support
* [drywolf](https://github.com/drywolf) for TSX file and better custom rule mapping

##With thanks
* The LCOV parser is directly copied from the community JavaScript SonarQube plug-in, which is LGPL'd.
