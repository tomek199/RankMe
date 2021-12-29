FROM openjdk:11-jdk
RUN apt-get update && apt-get install -y netcat
ADD build/libs/config-service*.jar config-service.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]