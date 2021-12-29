#!/bin/bash

RABBITMQ=(${WAIT_RABBITMQ//:/ })

while ! nc -z ${RABBITMQ[0]} ${RABBITMQ[1]}; do
  echo "Waiting for $WAIT_RABBITMQ"
  sleep 5;
done;

java -jar /config-service.jar