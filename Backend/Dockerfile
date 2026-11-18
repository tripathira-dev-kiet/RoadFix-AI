# Stage 1: Build the Spring Boot Application
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build jar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Production JRE 21 Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy generated jar from build stage
COPY --from=build /app/target/roadfix-backend-*.jar app.jar

# Dynamic PORT
EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
