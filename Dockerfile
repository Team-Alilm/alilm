FROM openjdk:17-alpine3.14
ARG JAR_FILE=/build/libs/Alilm-Be-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]