# ============================================================
# Dockerfile - Railway Deployment
# Multi-stage build: Frontend (Vue) + Backend (Spring Boot)
# ============================================================

# ---------- Stage 1: Build Vue Frontend ----------
FROM node:18-alpine AS frontend
WORKDIR /app/web
COPY web/package.json web/package-lock.json* ./
RUN npm install
COPY web/ .
RUN npm run build

# ---------- Stage 2: Build Spring Boot Backend ----------
FROM maven:3.9-eclipse-temurin-17 AS backend
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mkdir -p ./src/main/resources/static
COPY --from=frontend /app/web/dist ./src/main/resources/static
RUN mvn package -DskipTests -B -q

# ---------- Stage 3: Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=backend /app/target/*.jar app.jar
ENV PORT=8080
USER appuser
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]