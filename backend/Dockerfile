# ── Build stage ────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Cache dependencies separately from source
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q package -DskipTests

# ── Runtime stage ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/target/*.jar app.jar

# Persist seeded state and audit log here (mount a volume in compose)
RUN mkdir -p /app/data
VOLUME ["/app/data"]

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
