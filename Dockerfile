# Build frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY frontend/package*.json ./
RUN npm ci
COPY frontend/ ./
RUN npm run build

# Build backend
FROM eclipse-temurin:21-jdk-alpine AS backend-build
WORKDIR /app

# Copy maven wrapper and pom
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./

# Download dependencies (cached layer)
RUN ./mvnw dependency:go-offline -B

# Copy source
COPY backend/src/ src/

# Copy frontend build to static resources
COPY --from=frontend-build /app/frontend/dist/ src/main/resources/static/

# Build
RUN ./mvnw package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 appgroup && adduser -u 1001 -G appgroup -D appuser

# Copy built artifact
COPY --from=backend-build /app/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app
USER appuser

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
