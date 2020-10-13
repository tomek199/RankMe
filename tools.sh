#!/bin/bash

function sonar-develop() {
  gradle clean build sonarqube -Dsonar.branch.name=develop
}

function sonar-branch() {
  gradle clean build sonarqube -Dsonar.branch.name=$1 -Dsonar.branch.target=develop
}

"$@"