#!/bin/zsh
set -e

# Colores
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # Sin color

print_message() {
    echo ""
    echo -e "${GREEN}$1${NC}"
    echo ""
}

# Función para manejar errores
error_exit() {
    echo "${RED}Se ha producido un error. Abortando el script.${NC}" >&2
    exit 1
}

# Trampa para capturar cualquier error y llamar a la función 'error_exit'
trap error_exit ERR


# Validar si Docker está instalado
if ! command -v docker &> /dev/null
then
    echo "${RED}Docker no está instalado. Por favor, instálalo para continuar.${NC}"
    exit 1
fi

# Nombre de tu aplicación
APP_NAME="Cookers"
# Nombre de la imagen Docker
DOCKER_IMAGE_NAME="cookers-app"
# Obtener el hash del último commit de Git
COMMIT_HASH=$(git rev-parse --short HEAD)
# Puerto en el que tu aplicación estará escuchando
PORT=8080

# Secretos de Docker
SECRET_DB_URI="MONGO_URI_WHATACOOK_USERS"
SECRET_GMAIL_PASS="GMAIL_APP_PASSWORD"
SECRET_SMAIL_MAIL="SPRING_MAIL_VALIDATION"
SECRET_JWT="JWT_SECRET"

# Paso 1: Construir el proyecto con Gradle
print_message "Construyendo proyecto con Gradle..."
./gradlew bootJar

# Verifica si la construcción fue exitosa
# shellcheck disable=SC2181
if [ $? -ne 0 ]; then
    echo "${RED}La construcción del proyecto falló. Abortando despliegue.${NC}"
    exit 1
fi

# Paso 2: Construir la imagen Docker
print_message "Construyendo imagen Docker $DOCKER_IMAGE_NAME..."
docker build -t $DOCKER_IMAGE_NAME:"$COMMIT_HASH" .

# Verificar si el servicio ya existe
SERVICE_EXISTS=$(docker service ls | grep $APP_NAME | wc -l)

# Paso 3: Crear o actualizar el servicio con los secrets
if [ "$SERVICE_EXISTS" -eq 0 ]; then
    print_message "Creando nuevo servicio $APP_NAME..."
    docker service create --name $APP_NAME \
      --secret $SECRET_DB_URI \
      --secret $SECRET_GMAIL_PASS \
      --secret $SECRET_SMAIL_MAIL \
      --secret $SECRET_JWT \
      -p $PORT:$PORT \
      $DOCKER_IMAGE_NAME:"$COMMIT_HASH"
else
    print_message "Actualizando servicio existente $APP_NAME..."
    docker service update --force \
      --secret-rm $SECRET_DB_URI \
      --secret-rm $SECRET_GMAIL_PASS \
      --secret-rm $SECRET_SMAIL_MAIL \
      --secret-rm $SECRET_JWT \
      --secret-add source=$SECRET_DB_URI,target=$SECRET_DB_URI \
      --secret-add source=$SECRET_GMAIL_PASS,target=$SECRET_GMAIL_PASS \
      --secret-add source=$SECRET_SMAIL_MAIL,target=$SECRET_SMAIL_MAIL \
      --secret-add source=$SECRET_JWT,target=$SECRET_JWT \
      $APP_NAME
fi

# Después de crear o actualizar el servicio
if docker service ls | grep -q $APP_NAME; then
    print_message "El servicio $APP_NAME está funcionando."
else
    echo "${RED}No se pudo iniciar el servicio $APP_NAME.${NC}"
    exit 1
fi



print_message "Aplicación $APP_NAME desplegada exitosamente."
