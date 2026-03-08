# Stage 1: Build
FROM maven:3.9.0-openjdk-17 as builder

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
FROM eclipse-temurin:17-jre-alpine

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

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
