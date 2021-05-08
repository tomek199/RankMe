FROM openjdk:11-jdk
RUN apt-get update && apt-get install -y netcat
ADD build/libs/api-service*.jar api-service.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]