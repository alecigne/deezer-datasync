FROM --platform=$BUILDPLATFORM docker.io/eclipse-temurin:17-jdk-alpine AS build-stage
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean verify

# Alpine + JRE installation just to use an image that supports many architectures
FROM docker.io/library/debian:bullseye-slim
RUN apt update && apt install -y openjdk-17-jre-headless && apt clean
RUN useradd -ms /bin/bash appuser
USER appuser
WORKDIR /app
COPY --from=build-stage /app/target/deezer-datasync-with-deps.jar ./
CMD ["java", "-Dfile.encoding=utf-8", "-Dconfig.file=/application.conf", "-jar", "/app/deezer-datasync-with-deps.jar"]
