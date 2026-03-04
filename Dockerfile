# ---- Build stage ----
FROM gradle:9.2.1-jdk25 AS build
WORKDIR /build

ARG OTEL_VERSION=2.25.0

USER root
RUN apt-get update && apt-get install -y curl

RUN curl -L -s -o opentelemetry-javaagent.jar \
    https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v${OTEL_VERSION}/opentelemetry-javaagent.jar

RUN curl -L -s -o pyroscope.jar \
    https://github.com/pyroscope-io/pyroscope-java/releases/latest/download/pyroscope.jar

COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle
COPY src src

RUN chmod +x ./gradlew

RUN ./gradlew clean bootJar -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /build/build/libs/*.jar app.jar
COPY --from=build /build/opentelemetry-javaagent.jar opentelemetry-javaagent.jar
COPY --from=build /build/pyroscope.jar pyroscope.jar

ENV SPRING_PROFILES_ACTIVE=sit

# Команда запуска
CMD ["java", "-javaagent:/app/pyroscope.jar", "-javaagent:/app/opentelemetry-javaagent.jar", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]