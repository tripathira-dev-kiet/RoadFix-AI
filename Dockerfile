# Stage 1: Build the Spring Boot Application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy Backend pom and download dependencies
COPY Backend/pom.xml ./Backend/
WORKDIR /app/Backend
RUN mvn dependency:go-offline -B

# Copy source code and build jar
COPY Backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production JRE 21 Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy generated jar from build stage
COPY --from=build /app/Backend/target/roadfix-backend-*.jar app.jar

# Render / Railway dynamically assign PORT
EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
