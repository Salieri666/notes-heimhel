# ---- Build stage ----
FROM gradle:9.2.1-jdk25 AS build
WORKDIR /build

# Копируем файлы проекта
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle
COPY src src

RUN chmod +x ./gradlew

# Сборка jar без тестов
RUN ./gradlew clean bootJar -x test --no-daemon

# ---- Run stage ----
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Копируем jar из build stage
COPY --from=build /build/build/libs/*.jar app.jar

# ENV переменная для профиля Spring
ENV SPRING_PROFILES_ACTIVE=sit

# Команда запуска приложения
CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]
