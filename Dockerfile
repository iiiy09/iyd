# ============================================================
# Dockerfile — Railway Deployment
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
# Copy pom.xml first for dependency caching
COPY backend/pom.xml .
RUN mvn dependency:go-offline -B
# Copy source code
COPY backend/src ./src
# Copy built frontend into Spring Boot's static resources
RUN mkdir -p ./src/main/resources/static
COPY --from=frontend /app/web/dist ./src/main/resources/static
# Package the application
RUN mvn package -DskipTests -B

# ---------- Stage 3: Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the JAR from build stage
COPY --from=backend /app/target/*.jar app.jar

# Use Railway's PORT environment variable (default 8080)
ENV PORT=8080

# Switch to non-root user
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT}/ || exit 1

EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
