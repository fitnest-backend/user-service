# -----------------------------
# Stage 1: Build the Java JAR
# -----------------------------
FROM gradle:8.3.3-jdk17-alpine AS builder

# Set working directory
WORKDIR /app

# Copy Gradle project files
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# Copy source code
COPY src ./src

# Build the JAR (replace 'user-service' with your project name if different)
RUN gradle clean build -x test --no-daemon

# -----------------------------
# Stage 2: Package minimal image
# -----------------------------
FROM gcr.io/distroless/java17-debian12@sha256:fd925ba431f3a6c1f1c8114ce1999ca38803220baf0fdf25a4c71b38db8af67f

WORKDIR /app

# Copy the JAR from builder
COPY --from=builder /app/build/libs/user-service.jar app.jar

# Expose the port
EXPOSE 8080

# Run the app
ENTRYPOINT [ \
  "java", \
  "-XX:MaxRAMPercentage=70.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/urandom", \
  "-jar", \
  "app.jar" \
]