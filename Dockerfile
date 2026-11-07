# Single-image deployment build: the React app is bundled inside the Spring
# Boot jar so ONE container serves both the UI and the /api (REST + SSE) on one
# port. Used for cloud hosting (see render.yaml). Local development still uses
# the two separate Dockerfiles via docker-compose.yml.

# ===== Stage 1: build the React frontend =====
FROM node:22-alpine AS frontend
WORKDIR /fe
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build          # -> /fe/dist

# ===== Stage 2: build the backend with the SPA bundled into static resources =====
FROM maven:3.9-eclipse-temurin-21 AS backend
WORKDIR /be
COPY backend/pom.xml .
RUN mvn -B -q dependency:go-offline
COPY backend/src ./src
# Spring Boot serves classpath:/static/** at the web root, so the built SPA
# lands there and is packaged into the fat jar.
COPY --from=frontend /fe/dist ./src/main/resources/static
RUN mvn -B -q clean package -DskipTests

# ===== Stage 3: runtime =====
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /be/target/*.jar app.jar
# State + audit log are written under ./data (ephemeral on free hosting).
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
