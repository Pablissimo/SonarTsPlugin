SonarTsPlugin
=============

SonarQube plugin for TypeScript files

[![Build Status](https://travis-ci.org/Pablissimo/SonarTsPlugin.svg?branch=master)](https://travis-ci.org/Pablissimo/SonarTsPlugin)
[![Coverage Status](https://coveralls.io/repos/Pablissimo/SonarTsPlugin/badge.svg?branch=master)](https://coveralls.io/r/Pablissimo/SonarTsPlugin?branch=master)

***For SonarQube 6.0 support (or errors about 'MethodNotFound' exceptions) see the SonarQube 6.0 section at the bottom of the README***

##Demos

A live deployed demo hitting a few large open-source TypeScript projects can be found here:
[https://sonar.pablissimo.com](https://sonar.pablissimo.com).

Suggestions for more projects (or ones with easy-to-gather code coverage info) appreciated!

* [Microsoft TypeScript Compiler](https://sonar.pablissimo.com/overview?id=18739)
* [Turbulenz Engine](https://sonar.pablissimo.com/overview?id=20352)
* [Microsoft Visual Studio Code](https://sonar.pablissimo.com/overview?id=19179)
* [Angular Framework](https://sonar.pablissimo.com/overview?id=18822)

###Integrations		

* [Running TSLint within SonarQube on a TFS build](http://blogs.blackmarble.co.uk/blogs/rfennell/post/2016/07/05/Running-TSLint-within-SonarQube-on-a-TFS-build) - [Richard Fennell](https://github.com/rfennell)

##Overview

This is plugin for SonarQube 5.6+ for analysing projects with TypeScript content that supports:
* TsLint for code quality information
* Importing LCOV files for unit test coverage information
* NCLOC metric generation

It's unfinished in the following respects:
* Incomplete unit test coverage of the plugin
* No support for code duplication metrics

It's presented only for the interested, and the brave.

##Requirements
* Java 1.8+
* SonarQube 5.4 LTS+
* TsLint 2.4.0+

##Building
* Download the source
* Build with maven, *mvn clean && mvn install*

##Installation
* Install Node.js
* Install TsLint (2.4.0+) with `npm install -g tslint`, or ensure it is installed locally against your project
  * If you're installing globally, find the path to TsLint and copy it - will be similar to ```C:\Users\\[Username]\AppData\Roaming\npm\node_modules\tslint\bin\tslint``` on Windows
* Copy .jar file (from ```target/``` after build, or downloaded from [Releases page](https://github.com/Pablissimo/SonarTsPlugin/releases)) to SonarQube extensions folder
* Restart SonarQube server
* Browse to SonarQube web interface, login as Admin, hit up Settings
* Find the TypeScript tab, paste in the TsLint path
* Hit the Rules tab, then the TsLint rule set, then apply it to your project - alter rule activation as required
* Make sure you have a ```tslint.json``` file next to ```sonar-project.properties```, or specify its path using the ```sonar.ts.tslintconfigpath``` setting
* If LCOV data available, add *sonar.ts.lcov.reportpath=lcov.dat* to your sonar-project.properties file (replace lcov.dat with your lcov output, will be sought relative to the sonar-project.properties file)
* Run ```sonar-runner``` or ```sonar-scanner```
* TsLint rule breaches should be shown in the web view

##Configuration

###Global configuration

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th></thead>
<tbody>
<tr><td>sonar.ts.tslintpath</td><td><b>Recommended</b></td><td>Path to the installed copy of TsLint to use - can also be set at project level, see note below</td></tr>
<tr><td>sonar.ts.ruleconfigs</td><td><b>Optional</b></td><td>A list of configurations to map custom TsLint rules to dedicated SonarQube rules &amp; settings - see TsLint Custom Rules section below</td></tr>
</tbody>
</table>

###Project-level configuration

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th>
</thead>
<tbody>
<tr><td>sonar.ts.tslintpath</td><td><b>Recommended</b></td><td>Path to the installed copy of TsLint to use - see note below</td></tr>
<tr><td>sonar.ts.tslintconfigpath</td><td><b>Recommended</b></td><td>Path to the tslint.json file that configures the rules to be used in linting - see note below</td></tr>
<tr><td>sonar.ts.excludetypedefinitionfiles</td><td><b>Optional</b></td><td>Excludes .d.ts files from analysis, defaults to true</td></tr>
<tr><td>sonar.ts.forceZeroCoverage</td><td><b>Optional</b></td><td>Forces code coverage percentage to zero when no report is supplied, defaults to false</td></tr>
<tr><td>sonar.ts.ignoreNotFound</td><td><b>Optional</b></td><td>Don't set code coverage percentage to zero when file is not found in report, defaults to false</td></tr>
<tr><td>sonar.ts.tslinttimeout</td><td><b>Optional</b></td><td>Max time to wait for TsLint to finish processing a single file (in milliseconds), defaults to 60 seconds</td></tr>
<tr><td>sonar.ts.tslintrulesdir</td><td><b>Optional</b></td><td>Path to a folder containing custom TsLint rules referenced in tslint.json</td></tr>
<tr><td>sonar.ts.lcov.reportpath</td><td><b>Optional</b></td><td>Path to an LCOV code-coverage report to be included in analysis</td></tr>
</tbody>
</table>

##TsLint installation and configuration
By default, SonarTsPlugin will look for a version of TsLint installed locally within your project (i.e. in node_modules\tslint\bin), relative to the sonar-project.properties file. This may not be what you want, so you can set this directly via the ```sonar.ts.tslintpath``` configuration setting:
* At project level
* Globally, for all projects

If analysis is failing, run ```sonar-runner``` with the ```-X -e``` options for more diagnostic information, including a note of where the plugin is searching for ```tslint```. Bear in mind that if running on a build server, the account running the build will need access to the path to ```tslint```.

By default, SonarTsPlugin will look for a TsLint configuration file called tslint.json next to the sonar-project.properties file. You can override this using the ```sonar.ts.tslintconfigpath``` configuration setting if this isn't the case for your project.

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

##SonarQube 6.0 support
There is a **very preliminary** version of the plugin aimed at supporting SonarQube 6.0 (and nothing lower than 5.6 LTS) here:

https://github.com/Pablissimo/SonarTsPlugin/releases/tag/v0.93

There are almost certainly regressions due to the scale of changes that were required, but it's worth giving a go on a test install of 6.0 if you're planning on using it or getting any 'MethodNotFound' exceptions (which are the symptom of 0.9 and below's incompatibility).

##Licence
MIT

##Contributors
Thanks to the following for contributions to the plugin:
* [Alex Krauss](https://github.com/alexkrauss) and [Manuel Huber](https://github.com/nelo112) for work on improving compatibility with *nix OSes
* [schnee3](https://github.com/schnee3) for giving us some NCLOC support
* [drywolf](https://github.com/drywolf) for TSX file and better custom rule mapping

##With thanks
* The LCOV parser is directly copied from the community JavaScript SonarQube plug-in, which is LGPL'd.
