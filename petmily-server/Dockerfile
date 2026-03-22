FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar
LABEL authors="LSH"

ENTRYPOINT ["java","-jar","/app.jar"]