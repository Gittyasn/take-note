# Build stage: Compile code using Maven and JDK 17
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests=true

# Run stage: Create lightweight runtime image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/Take-Note-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
