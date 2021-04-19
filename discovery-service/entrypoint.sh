#!/bin/bash

CONFIG_SERVICE=(${WAIT_CONFIG_SERVICE//:/ })

while ! nc -z ${CONFIG_SERVICE[0]} ${CONFIG_SERVICE[1]}; do
  echo "Waiting for $WAIT_CONFIG_SERVICE"
  sleep 5;
done;

java -jar /discovery-service.jar