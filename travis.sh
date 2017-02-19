#!/bin/bash

if [ "$TRAVIS_BRANCH" == "master" ] && [ "$TRAVIS_PULL_REQUEST" == "false" ]; then
  mvn sonar:sonar \
      -Dsonar.host.url=$SONAR_URL \
      -Dsonar.login=$SONAR_TOKEN \
      -Dsonar.language=java \
      -Dsonar.projectVersion=$version\
      -Dsonar.scanner.skip=false
fi
