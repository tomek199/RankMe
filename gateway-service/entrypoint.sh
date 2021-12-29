#!/bin/bash

RABBITMQ=(${WAIT_RABBITMQ//:/ })
CONFIG_SERVICE=(${WAIT_CONFIG_SERVICE//:/ })
DISCOVERY_SERVICE=(${WAIT_DISCOVERY_SERVICE//:/ })

while ! nc -z ${RABBITMQ[0]} ${RABBITMQ[1]}; do
  echo "Waiting for $WAIT_RABBITMQ"
  sleep 5;
done;

while ! nc -z ${CONFIG_SERVICE[0]} ${CONFIG_SERVICE[1]}; do
  echo "Waiting for $WAIT_CONFIG_SERVICE"
  sleep 5;
done;

while ! nc -z ${DISCOVERY_SERVICE[0]} ${DISCOVERY_SERVICE[1]}; do
  echo "Waiting for $WAIT_DISCOVERY_SERVICE"
  sleep 5;
done;

java -jar /gateway-service.jar