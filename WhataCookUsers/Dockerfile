# Etapa 1: Construcción -> builder-tools
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

# Copia el archivo JAR de la etapa de construcción -> builder-tools
#COPY --from=builder /app/build/libs/cookers-0.0.1-SNAPSHOT.jar /app.jar

# --build-arg
ARG GMAIL_APP_PASSWORD
ARG JWT_SECRET
ARG MONGO_URI_WHATACOOK_USERS
ARG SPRING_MAIL_VALIDATION
# Argumento para el nombre del archivo JAR
ARG JAR_FILE=build/libs/cookers-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# Definir variables de entorno en Tiempo de Ejecución
ENV GMAIL_APP_PASSWORD=${GMAIL_APP_PASSWORD}
ENV JWT_SECRET=${JWT_SECRET}
ENV MONGO_URI_WHATACOOK_USERS=${MONGO_URI_WHATACOOK_USERS}
ENV SPRING_MAIL_VALIDATION=${SPRING_MAIL_VALIDATION}

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
