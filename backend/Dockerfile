# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /build

# Cache dependencies in a separate layer
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ===== Runtime stage =====
FROM eclipse-temurin:21-jre
WORKDIR /app

# The fat JAR has a versioned name; rename it to a stable filename
COPY --from=builder /build/target/*.jar app.jar

# Persist state.json + audit.jsonl via a mounted volume
VOLUME /app/data

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]