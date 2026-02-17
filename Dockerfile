# -----------------------------
# Stage 1: Build JAR
# -----------------------------
FROM gradle:8.5.0-jdk17 AS builder
WORKDIR /app

# Cache dependencies
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src/main/proto src/main/proto
RUN ./gradlew dependencies --no-daemon

# Copy source and build
COPY . .
RUN ./gradlew clean bootJar --no-build-cache --no-daemon

# -----------------------------
# Stage 2: Runtime image
# -----------------------------
FROM eclipse-temurin:17-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

# ENTRYPOINT must start at column 0, JSON array items too
ENTRYPOINT ["java", \
"-XX:+UseContainerSupport", \
"-XX:MaxRAMPercentage=75.0", \
"-XX:InitialRAMPercentage=50.0", \
"-XX:+UseG1GC", \
"-XX:+AlwaysPreTouch", \
"-XX:+ExitOnOutOfMemoryError", \
"-jar", \
"app.jar"]
