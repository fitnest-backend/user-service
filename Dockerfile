# -----------------------------
# Stage 1: Build JAR
# -----------------------------
FROM gradle:8.5.0-jdk17-alpine AS builder
WORKDIR /app

# Copy wrapper + gradle config first (better layer caching)
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
# If these exist in your repo, they are often required:
# COPY gradle.properties ./
# COPY libs.versions.toml gradle/libs.versions.toml

# Make wrapper executable and verify toolchain
RUN chmod +x gradlew
RUN ./gradlew --version

# Warm dependency cache (optional but fine)
RUN ./gradlew dependencies --no-daemon

# Copy the rest of the project (not only src)
COPY . .

# Build (add stacktrace for useful logs)
RUN ./gradlew clean bootJar -x test --no-daemon --stacktrace

# -----------------------------
# Stage 2: Runtime image
# -----------------------------
FROM eclipse-temurin:17.0.10_7-jre-alpine
WORKDIR /app

# Copy built jar
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-XX:InitialRAMPercentage=50.0", "-XX:+UseG1GC", "-XX:+AlwaysPreTouch", "-XX:+ExitOnOutOfMemoryError", "-jar", "app.jar"]
