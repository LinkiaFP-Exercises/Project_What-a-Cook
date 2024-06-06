#!/bin/zsh

# Función para cargar variables de entorno desde un archivo .env
load_env() {
  set -a
  source .env
  set +a
}

# Cargar las variables de entorno desde el archivo .env
# de momento no es necesario por cargar via gradle
#load_env

# Navegar a cada directorio y construir el archivo JAR
echo "Building whatacook-users..."
cd WhataCookUsers || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_USERS -Dspring.mail.password=$GMAIL_APP_PASSWORD -Djwt.secret=$JWT_SECRET -Dspring.mail.username=$SPRING_MAIL_VALIDATION

echo "Building whatacook-recipes..."
cd ../WhataCookRecipies || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_RECIPIES

# Volver al directorio raíz
cd ..

# Ejecutar Docker Compose
docker-compose -f compose-dev.yml up --build -d
