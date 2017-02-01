SonarEsLintPlugin
=============

SonarQube plugin for EsLint linter and its extensions. The code is based on the TypeScript plugin published by Pablissimo. [https://github.com/Pablissimo/SonarEsLintPlugin](https://github.com/Pablissimo/SonarEsLintPlugin)


Basically this plugin launches EsLint and colelcts its results into SonarQube. It may be slower than the own SonarQube javascript parser that I recommend to use for most usages. However if you are interested by AngularJS coding rules, uses my plugin :-)

[![Build Status](https://travis-ci.org/sleroy/SonarEsLintPlugin.svg?branch=master)](https://travis-ci.org/sleroy/SonarEsLintPlugin)
[![Coverage Status](https://coveralls.io/repos/github/sleroy/SonarEsLintPlugin/badge.svg?branch=master)](https://coveralls.io/github/sleroy/SonarEsLintPlugin?branch=master)


##Overview

This is plugin for SonarQube 5.6+ for analysing projects with Javascript content that supports:
* EsLint for code quality information
* EsLint angularJS extension

It's presented only for the interested, and the brave.

##Requirements
* Java 1.8+
* SonarQube 5.6 LTS+
* EsLint 3+ (Tested on 3.14)

##Building
* Download the source
* Build with maven, *mvn clean && mvn install*

##Installation
* Install Node.js
* Install EsLint (3+) with `npm install -g eslint`, or ensure it is installed locally against your project
  * If you're installing globally, find the path to EsLint and copy it - will be similar to ```C:\Users\\[Username]\AppData\Roaming\npm\node_modules\eslint\bin\eslint.js``` on Windows
* Copy .jar file (from ```target/``` after build, or downloaded from [Releases page](https://github.com/sleroy/SonarEsLintPlugin/releases)) to SonarQube extensions folder
* Restart SonarQube server
* Browse to SonarQube web interface, login as Admin, hit up Settings
* Hit the Rules tab, then the EsLint rule set, then apply it to your project - alter rule activation as required

Optional steps :
* Find the Javascript tab, paste in the Javascript path

* Make sure you have a ```.eslintrc.*``` file next to ```sonar-project.properties```, or specify its path using the ```sonar.eslint.eslintconfigpath``` setting
* If LCOV data available, add *sonar.ts.lcov.reportpath=lcov.dat* to your sonar-project.properties file (replace lcov.dat with your lcov output, will be sought relative to the sonar-project.properties file)
* Run ```sonar-runner``` or ```sonar-scanner```
* EsLint rule breaches should be shown in the web view

##Configuration

###Example project configuration
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
sonar.eslint.eslintconfigpath=eslint.json

```
- See the [Analysis Parameters](http://docs.sonarqube.org/display/SONAR/Analysis+Parameters) documentation page for general configuration options.
- See the [Narrowing the Focus](http://docs.sonarqube.org/display/SONAR/Narrowing+the+Focus) documentation page for configuration options related to which files to include.
- See the rest of this README for the SonarEsLintPlugin specific configuration options. 

###Global configuration options

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th></thead>
<tbody>
<tr><td>sonar.eslint.eslintpath</td><td><b>Recommended</b></td><td>Path to the installed copy of EsLint to use - can also be set at project level, see note below</td></tr>
<tr><td>sonar.eslint.ruleconfigs</td><td><b>Optional</b></td><td>A list of configurations to map custom EsLint rules to dedicated SonarQube rules &amp; settings - see EsLint Custom Rules section below</td></tr>
</tbody>
</table>

###Project-level configuration options

<table>
<thead>
<tr><th>Key</th><th></th><th>Description</th>
</thead>
<tbody>
<tr><td>sonar.eslint.eslintpath</td><td><b>Recommended</b></td><td>Path to the installed copy of EsLint to use - see note below</td></tr>
<tr><td>sonar.eslint.eslintconfigpath</td><td><b>Recommended</b></td><td>Path to the eslint.json file that configures the rules to be used in linting - see note below</td></tr>
<tr><td>sonar.eslint.excludetypedefinitionfiles</td><td><b>Optional</b></td><td>Excludes .d.ts files from analysis, defaults to true</td></tr>
<tr><td>sonar.eslint.forceZeroCoverage</td><td><b>Optional</b></td><td>Forces code coverage percentage to zero when no report is supplied, defaults to false</td></tr>
<tr><td>sonar.eslint.ignoreNotFound</td><td><b>Optional</b></td><td>Don't set code coverage percentage to zero when file is not found in report, defaults to false</td></tr>
<tr><td>sonar.eslint.eslinttimeout</td><td><b>Optional</b></td><td>Max time to wait for EsLint to finish processing a single file (in milliseconds), defaults to 60 seconds</td></tr>
<tr><td>sonar.eslint.eslintrulesdir</td><td><b>Optional</b></td><td>Path to a folder containing custom EsLint rules referenced in eslint.json</td></tr>
</tbody>
</table>

##EsLint installation and configuration
By default, SonarEsLintPlugin will look for a version of EsLint installed locally within your project (i.e. in node_modules\eslint\bin), relative to the sonar-project.properties file. This may not be what you want, so you can set this directly via the ```sonar.ts.eslintpath``` configuration setting:
* At project level
* Globally, for all projects

If analysis is failing, run ```sonar-runner``` with the ```-X -e``` options for more diagnostic information, including a note of where the plugin is searching for ```eslint```. Bear in mind that if running on a build server, the account running the build will need access to the path to ```eslint```.

By default, SonarEsLintPlugin will look for a EsLint configuration file called eslint.json next to the sonar-project.properties file. You can override this using the ```sonar.ts.eslintconfigpath``` configuration setting if this isn't the case for your project.

## EsLint Custom Rules

To present custom EsLint rules in SonarQube analysis, you can provide a configuration that maps the EsLint rules from your `sonar.ts.eslintrulesdir`
directory to dedicated Sonar rules for analysis.
The configuration for a EsLint Sonar rule consists of a line declaring the EsLint rule id, a boolean switch to enable or disable the rule if needed
and some attached properties that are used by Sonar for analysis and reporting.

For example taking the `export-name` rule from the [eslint-microsoft-contrib](https://github.com/Microsoft/eslint-microsoft-contrib) package,
a configuration for that rule in SonarEsLintPlugin could look as follows:

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

##Licence
MIT

##Contributors
Thanks to the following for contributions to the plugin:
* [Paul O'Neill](https://github.com/Pablissimo) For the original plugin for typescript