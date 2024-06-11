# Etapa 1: Construcción
#FROM gradle:8.8-jdk21-alpine AS builder
#WORKDIR /app
#COPY . /app
#RUN gradle clean build -x test

# Etapa 2: Imagen de Ejecución
FROM openjdk@sha256:4522ad40a423352cee560bef06ef63cad7b7ccb861a015723b3f37c0bd40590d

LABEL authors="Fauno Guazina"
LABEL maintainer="https://about.me/prof.guazina"

# Puerto en el que corre la aplicación
ARG PORT=8080
EXPOSE ${PORT}

# --build-arg
ARG MONGO_URI_WHATACOOK_RECIPIES

# Argumento para el nombre del archivo JAR
ARG JAR_FILE=build/libs/WhataCookRecipes-0.0.1-SNAPSHOT.jar

# Copia el archivo JAR de la etapa de construcción
#COPY --from=builder /app/build/libs/WhataCookRecipes-0.0.1-SNAPSHOT.jar /app.jar
COPY ${JAR_FILE} app.jar

# Definir variables de entorno en Tiempo de Ejecución
ENV MONGO_URI_WHATACOOK_RECIPES=${MONGO_URI_WHATACOOK_RECIPES}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
