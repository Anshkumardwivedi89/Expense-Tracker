# Stage 1: Build
FROM maven:3.8.4-openjdk-17 as builder

WORKDIR /app

# Copy pom.xml
COPY pom.xml .

# Download dependencies (this layer caches if pom.xml hasn't changed)
RUN mvn dependency:resolve

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests -q

# Stage 2: Runtime
FROM openjdk:17-slim

WORKDIR /app

# Copy built JAR from builder stage
COPY --from=builder /app/target/expense-tracker-*.jar app.jar

# Create logs directory
RUN mkdir -p /app/logs

# Set default port
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
