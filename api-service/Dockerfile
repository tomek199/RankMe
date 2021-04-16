FROM openjdk:11-jdk
ADD build/libs/api-service*.jar api-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/api-service.jar"]