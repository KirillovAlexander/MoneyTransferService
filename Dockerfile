FROM openjdk:12-alpine

EXPOSE 5500

ADD target/CardTransfer-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]