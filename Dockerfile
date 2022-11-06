# Build-Stage
FROM maven:3-eclipse-temurin-17-alpine as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD . $HOME
RUN mvn package

# Running-Instance
FROM openjdk:17-jdk-alpine
ARG VERSION="1.2"
ARG RUNDIR="/spring-app"
ARG CONFIGDIR="/spring-config"

LABEL MAINTAINER="Ayokas"
LABEL VERSION="${VERSION}"
LABEL GITHUB="https://github.com/Ayokas/cloudflare-dnsupdate-service"

EXPOSE 8080/tcp

VOLUME "/spring-config"

ARG JAR_FILE=target/*.jar
ENV JARPATH=${RUNDIR}/app.jar

COPY --from=build /usr/app/target/cloudflare-dnsupdate-service-${VERSION}.jar ${JARPATH}
COPY --from=build /usr/app/src/main/resources/application.properties /spring-config/application.properties

ENV SPRING_CONFIG_NAME=application

WORKDIR "/spring-config"
ENTRYPOINT [ "java", "-jar", "/spring-app/app.jar" ]