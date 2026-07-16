# ============================================================
# Dockerfile - Railway Deployment
# Multi-stage: Vue + Spring Boot
# ============================================================

# ---------- Frontend Build ----------
FROM node:18-alpine AS frontend
WORKDIR /app
COPY web/package.json web/package-lock.json* ./web/
RUN cd web && npm install
COPY web/ ./web
RUN cd web && npm run build

# ---------- Backend Build ----------
FROM eclipse-temurin:17-jdk-alpine AS backend
COPY apache-maven-3.9.6 /opt/maven
RUN chmod -R +x /opt/maven/bin/
ENV MAVEN_HOME=/opt/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"
ENV MAVEN_OPTS="-Xmx512m"
RUN mkdir -p /root/.m2
RUN echo "<settings><mirrors><mirror><id>aliyun</id><url>https://maven.aliyun.com/repository/public</url><mirrorOf>central</mirrorOf></mirror></mirrors></settings>" > /root/.m2/settings.xml
WORKDIR /app
COPY backend/pom.xml .
COPY backend/src ./src
RUN mkdir -p ./src/main/resources/static
COPY --from=frontend /app/web/dist ./src/main/resources/static
RUN mvn package -DskipTests -B -q

# ---------- Runtime ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=backend /app/target/*.jar app.jar
ENV PORT=8080
USER appuser
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]