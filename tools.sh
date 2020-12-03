#!/bin/zsh

function sonar-develop() {
  echo "Generating sonar report for 'develop' branch"
  gradle clean build sonarqube -Dsonar.branch.name=develop
}

function sonar-branch() {
  branch=$(git rev-parse --abbrev-ref HEAD)
  echo "Generating sonar report for '$branch' branch"
  gradle clean build sonarqube -Dsonar.branch.name=$branch -Dsonar.branch.target=develop
}

"$@"