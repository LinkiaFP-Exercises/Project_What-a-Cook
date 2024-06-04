# Imagen base oficial de OpenJDK 21 y ligera
FROM openjdk:23-jdk-slim

LABEL authors="Fauno Guazina"
LABEL maintainer="https://about.me/prof.guazina"

# Puerto en el que corre la aplicación
ARG PORT=8080

# --build-arg
ARG GMAIL_APP_PASSWORD
ARG JWT_SECRET
ARG MONGO_URI_WHATACOOK_USERS
ARG SPRING_MAIL_VALIDATION

# Expone el puerto para permitir el acceso a la aplicación
EXPOSE ${PORT}

# Argumento para el nombre del archivo JAR
ARG JAR_FILE=build/libs/cookers-0.0.1-SNAPSHOT.jar

# Copia el archivo JAR en la imagen
COPY ${JAR_FILE} app.jar

# Definir variables de entorno en Tiempo de Ejecución
ENV GMAIL_APP_PASSWORD=${GMAIL_APP_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV MONGO_URI_WHATACOOK_USERS=${MONGO_URI_WHATACOOK_USERS}
ENV SPRING_MAIL_VALIDATION=${SPRING_MAIL_VALIDATION}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
