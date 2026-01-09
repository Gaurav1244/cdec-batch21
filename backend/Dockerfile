# -------- STAGE 1: Build --------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests


# -------- STAGE 2: Runtime --------
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=build /app/target/student-registration-backend-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

