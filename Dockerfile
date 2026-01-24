
FROM gcr.io/distroless/java17-debian12@sha256:fd925ba431f3a6c1f1c8114ce1999ca38803220baf0fdf25a4c71b38db8af67f

WORKDIR /app

COPY build/libs/user-service.jar app.jar

EXPOSE 8080

ENTRYPOINT [ \
  "java", \
  "-XX:MaxRAMPercentage=70.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/urandom", \
  "-jar", \
  "app.jar" \
]
