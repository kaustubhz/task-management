# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml first to cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Stage 2: Run ----
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built JAR from Stage 1
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8088

# Run with docker profile
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]
