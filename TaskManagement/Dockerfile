#FROM eclipse-temurin:24-jdk
#VOLUME /tmp
#COPY target/task-wiser.jar task-wiser.jar
#ENTRYPOINT ["java","-jar","/task-wiser.jar"]

# Step 1: Build stage
FROM maven:3.9.9-eclipse-temurin-24 AS build
WORKDIR /app

# Copy pom.xml and download dependencies first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the source code
COPY src ./src

# Build the project and skip tests
RUN mvn clean package -DskipTests

# Step 2: Runtime stage
FROM eclipse-temurin:24-jdk
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/task-wiser.jar task-wiser.jar

# Expose the port Spring Boot runs on
EXPOSE 8080

# Run the app
ENTRYPOINT ["java", "-jar", "/app/task-wiser.jar"]
