FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk-alpine AS build-stage
WORKDIR /app
COPY . .
RUN chmod +x ./mvnw
RUN ./mvnw clean verify

# Alpine + JRE installation just to use an image that supports many architectures
FROM debian:bullseye-slim
RUN apt update && apt install -y openjdk-17-jre-headless && apt clean
RUN useradd -ms /bin/bash appuser
USER appuser
WORKDIR /app
COPY --from=build-stage /app/target/deezerConfig-datasync-with-deps.jar ./
CMD ["java", "-Dfile.encoding=utf-8", "-Dconfig.file=/application.conf", "-jar", "/app/deezerConfig-datasync-with-deps.jar"]
