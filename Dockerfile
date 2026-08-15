# Build stage: Compile code using Maven and Eclipse Temurin JDK 17
FROM maven:3.8-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests=true

# Run stage: Create lightweight runtime image using Temurin JRE Alpine
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/Take-Note-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
