FROM openjdk:17-ea-11-jdk-slim
VOLUME /tmp
COPY build/libs/auth-0.0.1-SNAPSHOT.jar AuthServer.jar
COPY src/main/resources/apiEncryptionKey.jks src/main/resources/apiEncryptionKey.jks
ENTRYPOINT ["java","-jar","AuthServer.jar"]