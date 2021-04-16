FROM openjdk:11-jdk
ADD build/libs/discovery-service*.jar discovery-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/discovery-service.jar"]