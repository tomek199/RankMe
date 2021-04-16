FROM openjdk:11-jdk
ADD build/libs/query-service*.jar query-service.jar
ENTRYPOINT ["java", "-Djav1a.security.egd=file:/dev/./urandom", "-jar", "/query-service.jar"]