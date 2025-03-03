FROM openjdk:21
MAINTAINER ts4.projects.org
COPY target/TS4Consumer-1.0-SNAPSHOT-spring-boot.jar app.jar
EXPOSE 9100
ENTRYPOINT java -Dspring.profiles.active=local -jar app.jar