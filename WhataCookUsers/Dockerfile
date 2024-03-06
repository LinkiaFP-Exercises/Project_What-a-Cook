# Imagen base oficial de OpenJDK 21 y ligera
FROM openjdk:21-jdk-slim

LABEL authors="faunoguazina"

# Puerto en el que corre la aplicación
ARG PORT=8080

# Expone el puerto para permitir el acceso a la aplicación
EXPOSE ${PORT}

# Argumento para el nombre del archivo JAR
ARG JAR_FILE=build/libs/cookers-0.0.1-SNAPSHOT.jar

# Copia el archivo JAR en la imagen
COPY ${JAR_FILE} app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]
