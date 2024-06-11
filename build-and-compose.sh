#!/bin/zsh

# Función para cargar variables de entorno desde un archivo .env
load_env() {
  if [ -f .env ]; then
    echo "Loading environment variables from .env"
    export $(grep -v '^#' .env | awk -F= '{print $1}' | xargs -I {} bash -c 'echo "{}=\"$(grep -m 1 -oP "(?<=^{}=).*" .env)\""')
  else
    echo "Env file not found"
  fi
}

# Cargar las variables de entorno desde el archivo .env
#load_env


# Navegar a cada directorio y construir el archivo JAR
echo "Building whatacook-users..."
cd WhataCookUsers || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_USERS -Dspring.mail.password=$GMAIL_APP_PASSWORD -Djwt.secret=$JWT_SECRET -Dspring.mail.username=$SPRING_MAIL_VALIDATION

echo "Building whatacook-recipes..."
cd ../WhataCookRecipes || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_RECIPES

# Volver al directorio raíz
cd ..

# Ejecutar Docker Compose
docker-compose -f compose-dev.yml up --build -d
