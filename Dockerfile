#
# Build stage
#
FROM maven:3.8.3-openjdk-17 AS build

# Set the working directory to /app
WORKDIR /app

# copy the pom.xml file to download dependencies
COPY pom.xml ./

COPY ./api ./api
COPY ./config ./config
COPY ./core ./core
COPY ./data ./data
COPY ./gateway ./gateway
COPY ./service ./service

# Compile the application.
RUN mvn -Dmaven.test.skip=true package && cp service/target/authentication-service-0.0.1-SNAPSHOT.jar app.jar

#
# Package stage
#
FROM amazoncorretto:17-alpine3.14

# set deployment directory
WORKDIR /app

COPY --from=build /app/app.jar ./app.jar

ENV VAULT_TOKEN "null"

EXPOSE 8080

CMD ["java", "-jar", "/app/app.jar"]