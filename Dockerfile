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


#шрифты
#FROM eclipse-temurin:17-jre as runtime
#WORKDIR /app
#ENV DEBIAN_FRONTEND=noninteractive
#
#RUN apt-get update && \
#    apt-get install -y --no-install-recommends \
#        ca-certificates \
#        wget \
#        fontconfig \
#        libfreetype6 \
#        libjpeg-turbo8 \
#        libxrender1 \
#        libxext6 \
#        xfonts-75dpi \
#        xfonts-base \
#        cabextract \
#        xfonts-utils \
#        fonts-noto \
#        fonts-noto-cjk \
#        fonts-dejavu-core \
#        fonts-dejavu-extra \
#        fonts-crosextra-carlito \
#        fonts-crosextra-caladea && \
#    fc-cache -f -v && \
#    rm -rf /var/lib/apt/lists/*
#
## Автоматическое принятие EULA
#RUN echo "ttf-mscorefonts-installer msttcorefonts/accepted-mscorefonts-eula select true" | debconf-set-selections
#
## Установка MS Fonts
#RUN apt-get update && \
#    apt-get install -y ttf-mscorefonts-installer && \
#    fc-cache -f -v
#
## Установка wkhtmltopdf
#COPY document-template-service/wkhtmltox_0.12.6.1-2.jammy_amd64.deb /tmp/wkhtmltox.deb
#RUN dpkg -i /tmp/wkhtmltox.deb || (apt-get update && apt-get -f install -y) && \
#    rm -f /tmp/wkhtmltox.deb
#
#COPY --from=build /build/document-template-service/target/*smsfinance*.jar /app/app.jar
#
#ENV FONTCONFIG_PATH=/etc/fonts
#
#CMD ["java", "-Dspring.profiles.active=sit,native", "-jar", "app.jar"]

