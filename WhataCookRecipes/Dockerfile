# Imagen base oficial de OpenJDK 21 y ligera
FROM openjdk:23-jdk-slim

LABEL authors="Fauno Guazina"
LABEL maintainer="https://about.me/prof.guazina"

# Puerto en el que corre la aplicación
ARG PORT=8080

# --build-arg
ARG MONGO_URI_WHATACOOK_RECIPES

# Expone el puerto para permitir el acceso a la aplicación
EXPOSE ${PORT}

# Argumento para el nombre del archivo JAR
ARG JAR_FILE=build/libs/WhataCookRecipes-0.0.1-SNAPSHOT.jar

# Copia el archivo JAR en la imagen
COPY ${JAR_FILE} app.jar

# Definir variables de entorno en Tiempo de Ejecución
ENV MONGO_URI_WHATACOOK_RECIPES=${MONGO_URI_WHATACOOK_RECIPES}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
