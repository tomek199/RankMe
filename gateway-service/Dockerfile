FROM openjdk:11-jdk
ADD build/libs/gateway-service*.jar gateway-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/gateway-service.jar"]