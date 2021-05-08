#!/bin/bash

CONFIG_SERVICE=(${WAIT_CONFIG_SERVICE//:/ })
RABBITMQ=(${WAIT_RABBITMQ//:/ })

while ! nc -z ${CONFIG_SERVICE[0]} ${CONFIG_SERVICE[1]}; do
  echo "Waiting for $WAIT_CONFIG_SERVICE"
  sleep 5;
done;

while ! nc -z ${RABBITMQ[0]} ${RABBITMQ[1]}; do
  echo "Waiting for $WAIT_RABBITMQ"
  sleep 5;
done;

java -jar /api-service.jar