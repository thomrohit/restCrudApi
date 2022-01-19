FROM openjdk:8-jdk-alpine
COPY target/*.jar /opt/rest/app.jar
EXPOSE 8181
ENTRYPOINT ["java","-jar","/opt/rest/app.jar"]