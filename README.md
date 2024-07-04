
# WhataCook API

## 1. Introducción

**WhataCook** es una API diseñada para facilitar la planificación de comidas y reducir el desperdicio de alimentos, proporcionando recetas basadas en los ingredientes disponibles en la cocina de los usuarios. El backend está desarrollado en Java con Spring Boot/WebFlux, diseñada para gestionar recetas de cocina y usuarios de manera eficiente. Este proyecto implementa una arquitectura limpia y escalable, utilizando una base de datos NoSQL MongoDB para el almacenamiento de datos.

## Características Principales

- **Gestión de Usuarios:** Registro, autenticación y gestión de perfiles de usuario.
- **Gestión de Recetas:** Creación, actualización y eliminación de recetas con ingredientes y categorías.
- **Búsqueda Avanzada:** Filtrado de recetas por nombre, ingredientes y categorías.
- **Arquitectura Reactiva:** Implementación de patrones reactivos con Spring WebFlux.
- **Seguridad:** Autenticación y autorización mediante JWT.

## Tecnologías Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring WebFlux**
- **MongoDB**
- **JWT**
- **Docker**

## Estructura del Proyecto

El proyecto se organiza en dos módulos principales:

### Módulo de Usuarios (users-app)

- **Controladores:** Gestionan las solicitudes HTTP para operaciones de usuario.
- **Servicios:** Contienen la lógica de negocio relacionada con los usuarios.
- **Modelos:** Definen las estructuras de datos (DTOs) utilizadas en la aplicación.
- **Configuraciones:** Incluyen configuraciones de seguridad y JWT.

### Módulo de Recetas (recipes-app)

- **Controladores:** Gestionan las solicitudes HTTP para operaciones de recetas.
- **Servicios:** Contienen la lógica de negocio relacionada con las recetas.
- **Modelos:** Definen las estructuras de datos (DTOs) utilizadas en la aplicación.
- **Utilidades:** Incluyen funciones de ayuda para la paginación y ordenación.

## Instalación y Ejecución

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local:

### Prerrequisitos

- Docker

### Instrucciones

Para probar la aplicación WhataCook usando Docker, puedes construir y ejecutar un contenedor siguiendo estos pasos:

#### Construir la imagen con Docker Compose
1. Primero, crea un archivo 'docker-compose.yml' en el directorio raíz con el siguiente contenido:
    ```yaml
    services:
      whatacook-users:
        image: faunog/whatacook:cookers-app
        ports:
          - "8083:8080"
        pull_policy: always
    
      whatacook-recipes:
        image: faunog/whatacook:recipes-app
        ports:
          - "8082:8080"
        pull_policy: always
    ```
2. Desde la terminal, navega al directorio raíz de tu proyecto donde se encuentra el archivo docker-compose.yml y ejecuta:
   ```bash
    docker-compose up
    ```

    
#### Construir la imagen de cada modulo por separdo con Docker

1. Primero, construye la imagen Docker usando el siguiente comando:
    #### Descargar whatacook-users
    ```bash
    docker --pull faunog/whatacook:cookers-app
    ```
    #### Descargar whatacook-recipes
    ```bash
    docker --pull faunog/whatacook:recipes-app
    ```
2. Ejecutar la aplicación
Una vez construida la imagen, puedes ejecutar la aplicación utilizando Docker con el siguiente comando. Este comando inicia el contenedor y expone el puerto 8080, lo que permite acceder a la aplicación desde tu navegador o cliente HTTP:
    #### Descargar whatacook-users
    ```bash
    docker run -p 8083:8080 faunog/whatacook:cookers-app
    ```
    #### Descargar whatacook-recipes
    ```bash
    docker run -p 8082:8080 faunog/whatacook:recipes-app
    ```

>Para más detalles recomendamos consultar la documentación oficial de Docker.

## Documentación de la API - Postman requests

Puedes encontrar la colección de requests con detalles de como utilizar cada uno de los endpoints en el seguiente enlace:
[WhataCook - @Cookers-NewTests](https://documenter.getpostman.com/view/12946439/2sA35A8QuD)
