SonarTsPlugin
=============

SonarQube plugin for TypeScript files

[![Build Status](https://travis-ci.org/Pablissimo/SonarTsPlugin.svg?branch=master)](https://travis-ci.org/Pablissimo/SonarTsPlugin)
[![Coverage Status](https://coveralls.io/repos/github/Pablissimo/SonarTsPlugin/badge.svg?branch=master)](https://coveralls.io/github/Pablissimo/SonarTsPlugin?branch=master)

## Demos

A live deployed demo hitting a few varied TypeScript projects can be found here:
[https://sonar.pablissimo.com](https://sonar.pablissimo.com).

* [basic-setup](https://sonar.pablissimo.com/dashboard?id=com.pablissimo.sonar%3Abasic-setup)
* [using-existing-tslint-output](https://sonar.pablissimo.com/dashboard?id=com.pablissimo.sonar%3Ausing-existing-tslint-output)
* [Microsoft TypeScript Compiler](https://sonar.pablissimo.com/dashboard?id=TypeScript)

The plugin is tested against different permutations of the SonarQube server and scanner using the sample projects listed below. The test servers are as follows:

* [v5.6.6](https://v566.sonar.pablissimo.com)
* [v6.0](https://v60.sonar.pablissimo.com)
* [v6.1](https://v61.sonar.pablissimo.com)
* [v6.2](https://v62.sonar.pablissimo.com)
* [v6.3](https://v63.sonar.pablissimo.com)

## Sample projects
Some sample projects are provided to demonstrate different configuration options in the [samples/](https://github.com/Pablissimo/SonarTsPlugin/tree/master/samples) folder.

* [basic-setup](https://github.com/Pablissimo/SonarTsPlugin/tree/master/samples/basic-setup) - a standard `tslint` pass with code coverage
* [using-existing-tslint-output](https://github.com/Pablissimo/SonarTsPlugin/tree/master/samples/using-existing-tslint-output) - as above, but re-using the output of a build-step `tslint` pass rather than the plugin running it itself

## Integrations		

* [Running TSLint within SonarQube on a TFS build](http://blogs.blackmarble.co.uk/blogs/rfennell/post/2016/07/05/Running-TSLint-within-SonarQube-on-a-TFS-build) - [Richard Fennell](https://github.com/rfennell)

## Overview

This is plugin for SonarQube 5.6+ for analysing projects with TypeScript content that supports:
* TsLint for code quality information
* Importing LCOV files for unit test coverage information
* NCLOC metric generation

## Requirements
* Java 1.8+
* SonarQube 5.6 LTS+
* TsLint 2.4.0+

## Building
* Download the source
* Build with maven, *mvn clean && mvn install*

## Installation
* Install Node.js
* Install TsLint (2.4.0+) with `npm install -g tslint`, or ensure it is installed locally against your project
  * If you're installing globally, find the path to TsLint and copy it - will be similar to ```C:\Users\[Username]\AppData\Roaming\npm\node_modules\tslint\bin\tslint``` on Windows
* Copy .jar file (from ```target/``` after build, or downloaded from [Releases page](https://github.com/Pablissimo/SonarTsPlugin/releases)) to SonarQube extensions/plugins folder
* Restart SonarQube server
* Browse to SonarQube web interface, login as Admin, hit up Settings
* Find the TypeScript tab, paste in the TsLint path
* Hit the Rules tab, then the TsLint rule set, then apply it to your project - alter rule activation as required
* Make sure you have a ```tslint.json``` file next to ```sonar-project.properties```, or specify its path using the ```sonar.ts.tslint.configPath``` setting
* If LCOV data available, add *sonar.ts.coverage.lcovReportPath=lcov.dat* to your sonar-project.properties file (replace lcov.dat with your lcov output, will be sought relative to the sonar-project.properties file)
* Run ```sonar-runner``` or ```sonar-scanner```
* TsLint rule breaches should be shown in the web view

## Configuration

### Example project configuration
This is an example of what a project configuration file (`sonar-project.properties`) could look like:
```
sonar.projectKey=company:my-application
sonar.projectName=My Application
sonar.projectVersion=1.0
sonar.sourceEncoding=UTF-8
sonar.sources=src/app
sonar.exclusions=**/node_modules/**,**/*.spec.ts
sonar.tests=src/app
sonar.test.inclusions=**/*.spec.ts

sonar.ts.tslint.configPath=tslint.json
sonar.ts.coverage.lcovReportPath=test-results/coverage/coverage.lcov
```
- See the [Analysis Parameters](http://docs.sonarqube.org/display/SONAR/Analysis+Parameters) documentation page for general configuration options.
- See the [Narrowing the Focus](http://docs.sonarqube.org/display/SONAR/Narrowing+the+Focus) documentation page for configuration options related to which files to include.
- See the rest of this README for the SonarTsPlugin specific configuration options. 

### Global configuration options

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th></thead>
<tbody>
<tr><td>sonar.ts.tslint.ruleConfigs</td><td><b>Optional</b></td><td>A list of configurations to map custom TsLint rules to dedicated SonarQube rules &amp; settings - see TsLint Custom Rules section below</td></tr>
</tbody>
</table>

### Project-level configuration options

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th>
</thead>
<tbody>
<tr><td>sonar.ts.tslint.path</td><td><b>Optional</b></td><td>Path to the installed copy of `tslint` to use - will be automatically sought in node_modules next to the sonar-project.properties file if not specified</td></tr>
<tr><td>sonar.ts.tslint.configPath</td><td><b>Optional</b></td><td>Path to the tslint.json file that configures the rules to be used in linting - will be automatically sought in the same folder as the sonar-project.properties file if not specified</td></tr>
<tr><td>sonar.ts.tslint.outputPath</td><td><b>Optional</b></td><td>If your existing CI process already runs `tslint` you can have the plugin re-use its output using the `outputPath` setting. The output is expected to be in JSON form</td></tr>
<tr><td>sonar.ts.excludeTypeDefinitionFiles</td><td><b>Optional</b></td><td>Excludes .d.ts files from analysis, defaults to true</td></tr>
<tr><td>sonar.ts.coverage.forceZeroIfUnspecified</td><td><b>Optional</b></td><td>Forces code coverage percentage to zero for all files when no LCOV report is supplied, defaults to false</td></tr>
<tr><td>sonar.ts.coverage.ignoreNotFound</td><td><b>Optional</b></td><td>Controls if a single file should be reported as 0% covered if it doesn't appear in the LCOV report, defaults to false</td></tr>
<tr><td>sonar.ts.tslint.timeout</td><td><b>Optional</b></td><td>Max time to wait for `tslint` to finish processing a single file (in milliseconds), defaults to 60 seconds</td></tr>
<tr><td>sonar.ts.tslint.rulesDir</td><td><b>Optional</b></td><td>Path to a folder containing custom `tslint` rules referenced in tslint.json, if any is required</td></tr>
<tr><td>sonar.ts.coverage.lcovReportPath</td><td><b>Optional</b></td><td>Path to an LCOV code-coverage report to be used to calculate coverage metrics for your project</td></tr>
<tr><td>sonar.ts.tslint.nodePath</td><td><b>Optional</b></td><td>Path to custom node to execute</td></tr>
<tr><td>sonar.ts.tslint.projectPath</td><td><b>Optional</b></td><td>Path to tsconfig.json that describes the TypeScript files in your project to analyse, rather than letting SonarQube search for them automatically. Required to allow sonar.ts.tslint.typeCheck to work.</td></tr>
<tr><td>sonar.ts.tslint.typeCheck</td><td><b>Optional</b></td><td>If true, asks tslint to run type-checking, allowing tslint rules that need type information to operate. Requires that you have specified sonar.ts.tslint.projectPath.</td></tr>
</tbody>
</table>

## TsLint installation and configuration
By default, SonarTsPlugin will look for a version of TsLint installed locally within your project (i.e. in node_modules\tslint\bin), relative to the sonar-project.properties file. This may not be what you want, so you can set this directly via the ```sonar.ts.tslint.path``` configuration setting:
* At project level
* Globally, for all projects

If analysis is failing, run ```sonar-scanner``` with the ```-X``` option for more diagnostic information, including a note of where the plugin is searching for ```tslint```. Bear in mind that if running on a build server, the account running the build will need access to the path to ```tslint```.

By default, SonarTsPlugin will look for a `tslint` configuration file called tslint.json next to the sonar-project.properties file. You can override this using the ```sonar.ts.tslint.configPath``` configuration setting if this isn't the case for your project.

## TsLint Custom Rules

To present custom `tslint` rules in SonarQube analysis, you can provide a configuration that maps the rules from your `sonar.ts.tslint.rulesDir` directory to dedicated Sonar rules for analysis.

The configuration for a `tslint` Sonar rule consists of a line declaring the TSLint rule id, a boolean switch to enable or disable the rule if needed and some attached properties that are used by Sonar for analysis and reporting.

For example, let's take the `export-name` rule from the [tslint-microsoft-contrib](https://github.com/Microsoft/tslint-microsoft-contrib) package. A configuration for that rule in SonarTsPlugin could look as follows:

	export-name=true
	export-name.name=The name of the exported module must match the filename of the source file.
	export-name.severity=MAJOR
	export-name.description=This is case-sensitive but ignores file extension. Since version 1.0, this rule takes a list of regular expressions as a parameter. Any export name matching that regular expression will be ignored.
	export-name.debtFunc=LINEAR_OFFSET
	export-name.debtScalar=15min
	export-name.debtOffset=1h
	export-name.debtType=HARDWARE_RELATED_PORTABILITY

**You will need to restart the SonarQube server after configuring custom rules this way before subsequent analyses will pick them up. You will also need to activate the new rules after restart for any quality profile you want them to participate in - by default they will be disabled.**

* For documentation about the `technical debt` parameters look [here](http://docs.sonarqube.org/display/PLUG/Rule+Remediation+Costs) and [here](http://javadocs.sonarsource.org/5.2/apidocs/org/sonar/api/server/debt/DebtRemediationFunction.html)
* For possible values for `debtType` go [here](http://javadocs.sonarsource.org/5.2/apidocs/org/sonar/api/server/rule/RulesDefinition.SubCharacteristics.html)

## Licence
MIT

## Contributors
Thanks to the following for contributions to the plugin:
* [Alex Krauss](https://github.com/alexkrauss) and [Manuel Huber](https://github.com/nelo112) for work on improving compatibility with *nix OSes
* [schnee3](https://github.com/schnee3) for giving us some NCLOC support
* [drywolf](https://github.com/drywolf) for TSX file and better custom rule mapping
* [NikitaEgorov](https://github.com/NikitaEgorov) for changes to support consuming existing tslint output

## With thanks
* The LCOV parser is directly copied from the community JavaScript SonarQube plug-in, which is LGPL'd.
