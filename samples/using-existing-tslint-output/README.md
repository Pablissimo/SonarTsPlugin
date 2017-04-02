# Using existing tslint output example
This sample project can be analysed by SonarQube to demonstrate re-using the output of a build-step ```tslint``` pass, instead of having the plugin perform the analysis itself.

You can see a live example of the results of analysing this project at [https://sonar.pablissimo.com](https://sonar.pablissimo.com/dashboard?id=com.pablissimo.sonar%3Ausing-existing-tslint-output).

This sample is identical to the basic-setup sample but with reuse of tslint output.

## Building and analysing

If you want, you can just analyse this project directly as the tslint output has already been generated. However, to rebuild fully:

* Run ```npm install``` from the cloned repo folder
* Run ```npm test``` to run unit tests and ```tslint``` analysis, building the ```issues.json``` output file.

To analyse with SonarQube just run ```sonar-scanner -X``` from the cloned repo folder.

* The -X flag will give us diagnostic information during the run, so you can see what the plugin is up to

## Breaking down the sonar-project.properties file

The sample has a ```sonar-project.properties``` file that controls how the analysis gets run. This file differs from the basic-setup example only in one interesting respect:

<table>
<thead><tr><th>Line</th><th>Description</th></tr></thead>
<tbody>
<tr><td>sonar.ts.tslint.outputPath=issues.json</td><td>Tells the plugin to skip running tslint itself, and instead just parse the output of the previous tslint path found in the issues.json file</td></tr>
</tbody>
</table>

See the basic-setup example for detail on the other configured settings.

## Rationale
It's possible that your CI build already performs a ```tslint``` path, since a tslint failure might be considered important enough to break your build (or at least otherwise report on). Since a ```tslint``` path on a large project can take a while, the ```sonar.ts.tslint.outputPath``` setting can be set to reuse the output of the CI call to ```tslint```, reducing the time it takes to perform analysis.

It also allows you to call ```tslint``` with parameters or configuration that the plugin may not easily handle, or otherwise filter or transform the ```tslint``` output before it gets consumed by the plugin.
