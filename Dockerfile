# -----------------------------
# Stage 1: Build JAR
# -----------------------------
FROM gradle:9.3-jdk25 AS builder
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
FROM eclipse-temurin:25-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT [ \
  "java", \
  "-XX:MaxRAMPercentage=70", \
  "-XX:InitialRAMPercentage=50", \
  "-XX:+UseZGC", \
  "-Xss256k", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-XX:HeapDumpPath=/tmp/heapdump.hprof", \
  "-jar", "app.jar" \
]
