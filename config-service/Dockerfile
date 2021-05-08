FROM openjdk:11-jdk
ADD build/libs/config-service*.jar config-service.jar
ENTRYPOINT ["java", "-jar", "/config-service.jar"]