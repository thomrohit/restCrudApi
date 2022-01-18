#FROM openjdk:8-jdk-alpine
FROM registry.web.boeing.com/container/boeing-images/stack/ubi8minimal-jdk8
COPY target/*.jar app.jar
EXPOSE 8181
ENTRYPOINT ["java","-jar","/app.jar"]