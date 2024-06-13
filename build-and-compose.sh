#!/bin/zsh

# Funciones para los colores
print_blue() {
  echo -e "\033[1;34m$1\033[0m"
}

print_red() {
  echo -e "\033[1;31m$1\033[0m"
}

print_green() {
  echo -e "\033[1;32m$1\033[0m"
}

# Función para cargar variables de entorno desde un archivo .env
load_env() {
  if [ -f .env ]; then
    print_blue "Loading environment variables from .env"
    export $(grep -v '^#' .env | awk -F= '{print $1}' | xargs -I {} bash -c 'echo "{}=\"$(grep -m 1 -oP "(?<=^{}=).*" .env)\""')
  else
    print_red "Env file not found"
  fi
}

# Cargar las variables de entorno desde el archivo .env
#load_env

# Separador visual
print_green "=============================="

# Navegar a cada directorio y construir el archivo JAR
print_blue "Building whatacook-users..."
cd WhataCookUsers || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_USERS -Dspring.mail.password=$GMAIL_APP_PASSWORD -Djwt.secret=$JWT_SECRET -Dspring.mail.username=$SPRING_MAIL_VALIDATION

# Separador visual
print_green "=============================="

print_blue "Building whatacook-recipes..."
cd ../WhataCookRecipes || exit
./gradlew clean build --scan -Dspring.data.mongodb.uri=$MONGO_URI_WHATACOOK_RECIPES

# Separador visual
print_green "=============================="

# Volver al directorio raíz
cd ..

# Ejecutar Docker Compose
print_blue "Running Docker Compose..."
docker-compose -f compose-dev.yml up --build -d

# Separador visual final
print_green "=============================="
print_blue "Setup Complete!"
