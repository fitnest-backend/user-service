# -----------------------------
# Stage 1: Build JAR
# -----------------------------
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app

# Copy only build files first to cache dependencies
COPY gradlew /app/gradlew
COPY gradle /app/gradle
COPY build.gradle settings.gradle gradle.properties /app/

# Ensure wrapper is executable (common failure)
RUN chmod +x /app/gradlew

# Cache dependencies
RUN ./gradlew --no-daemon dependencies

# Copy sources and build
COPY src /app/src
RUN ./gradlew --no-daemon clean bootJar

# -----------------------------
# Stage 2: Runtime image
# -----------------------------
FROM eclipse-temurin:17.0.10_7-jre-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar /app/app.jar
EXPOSE 8080

ENTRYPOINT ["java",
  "-XX:+UseContainerSupport",
  "-XX:MaxRAMPercentage=75.0",
  "-XX:InitialRAMPercentage=50.0",
  "-XX:+UseG1GC",
  "-XX:+AlwaysPreTouch",
  "-XX:+ExitOnOutOfMemoryError",
  "-jar",
  "/app/app.jar"
]