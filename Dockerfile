## -----------------------------
## Stage 1: Build the Java JAR
## -----------------------------
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

# Copy Gradle project files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the JAR
RUN gradle clean build -x test --no-daemon

## -----------------------------
## Stage 2: Package minimal Alpine image
## -----------------------------
FROM eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.source=https://github.com/fitnest-backend/user-service

WORKDIR /app

# Copy the JAR from builder
COPY --from=builder /app/build/libs/*.jar app.jar

# Add curl for health checks (optional but helpful)
RUN apk add --no-cache curl

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

EXPOSE 8080

ENTRYPOINT [ \
  "java", \
  "-XX:MaxRAMPercentage=70.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/urandom", \
  "-Djava.net.preferIPv4Stack=true", \
  "-Djava.net.preferIPv4Addresses=true", \
  "-Dspring.profiles.active=dev", \
  "-jar", \
  "app.jar" \
]