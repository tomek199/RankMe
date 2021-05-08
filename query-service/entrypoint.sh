#!/bin/bash

CONFIG_SERVICE=(${WAIT_CONFIG_SERVICE//:/ })
RABBITMQ=(${WAIT_RABBITMQ//:/ })
MONGODB=(${WAIT_MONGODB//:/ })

while ! nc -z ${CONFIG_SERVICE[0]} ${CONFIG_SERVICE[1]}; do
  echo "Waiting for $WAIT_CONFIG_SERVICE"
  sleep 5;
done;

while ! nc -z ${RABBITMQ[0]} ${RABBITMQ[1]}; do
  echo "Waiting for $WAIT_RABBITMQ"
  sleep 5;
done;

while ! nc -z ${MONGODB[0]} ${MONGODB[1]}; do
  echo "Waiting for $WAIT_MONGODB"
  sleep 5;
done;

java -jar /query-service.jar