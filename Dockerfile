FROM openjdk:17-jdk-slim

WORKDIR /api

COPY target/KavaSpring-0.0.1-SNAPSHOT.jar /api/app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","/api/app.jar"]