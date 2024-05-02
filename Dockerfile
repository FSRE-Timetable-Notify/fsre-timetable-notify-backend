FROM eclipse-temurin:22-jdk-alpine AS build

WORKDIR /app

COPY .env .env
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean verify package spring-boot:repackage -DskipTests

FROM eclipse-temurin:22-jdk-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

VOLUME /tmp

WORKDIR /app

COPY service_account_key.json service_account_key.json

COPY --from=build /app/target/fsretimetablenotify-0.0.1-SNAPSHOT.jar fsretimetablenotify-0.0.1-SNAPSHOT.jar

EXPOSE 5000

ENTRYPOINT ["java", "-jar", "fsretimetablenotify-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=prod"]