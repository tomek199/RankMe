FROM openjdk:11-jdk
ADD build/libs/command-service*.jar command-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/command-service.jar"]