FROM openjdk:11-jdk
ADD build/libs/config-service*.jar config-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/config-service.jar"]