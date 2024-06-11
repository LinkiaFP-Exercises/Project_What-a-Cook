# Etapa 1: Construcción
FROM openjdk:21-ea-17-jdk-slim AS builder
WORKDIR /app
COPY . /app
RUN apt-get update && \
    apt-get install -y unzip && \
    apt-get clean
RUN ./gradlew clean build -x test

# Etapa 2: Imagen de Ejecución
FROM openjdk@sha256:e7f111d283eb8afcd74fbad6d9105b8e5d49022d8e0b21d199b939a71addf531

LABEL authors="Fauno Guazina"
LABEL maintainer="https://about.me/prof.guazina"

# Puerto en el que corre la aplicación
ARG PORT=8080

# Expone el puerto para permitir el acceso a la aplicación
EXPOSE ${PORT}

# Copia el archivo JAR de la etapa de construcción
COPY --from=builder /app/build/libs/cookers-0.0.1-SNAPSHOT.jar app.jar

# Definir variables de entorno en Tiempo de Ejecución
ENV GMAIL_APP_PASSWORD=${GMAIL_APP_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV MONGO_URI_WHATACOOK_USERS=${MONGO_URI_WHATACOOK_USERS}
ENV SPRING_MAIL_VALIDATION=${SPRING_MAIL_VALIDATION}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
