FROM openjdk:11-jdk
RUN apt-get update && apt-get install -y netcat
ADD build/libs/subscription-service*.jar subscription-service.jar
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]