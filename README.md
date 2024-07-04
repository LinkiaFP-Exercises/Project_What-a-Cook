
# WhataCook API

## Introducción

**WhataCook** es una API diseñada para facilitar la planificación de comidas y reducir el desperdicio de alimentos. Proporciona recetas basadas en los ingredientes disponibles en la cocina de los usuarios, permitiendo búsquedas específicas que incluyen todos los ingredientes indicados en la consulta. El backend está desarrollado en Java con Spring Boot/WebFlux, diseñado para gestionar recetas de cocina y usuarios de manera eficiente. Este proyecto implementa una arquitectura limpia y escalable, utilizando una base de datos NoSQL MongoDB para el almacenamiento de datos.

## Características Principales

- **Gestión de Usuarios:** Registro, autenticación y gestión de perfiles de usuario.
- **Gestión de Recetas:** Creación, actualización y eliminación de recetas con ingredientes y categorías.
- **Búsqueda Avanzada:** Filtrado de recetas por nombre y ingredientes.
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

- **com.whatacook.cookers:** Contiene las clases principales de la aplicación de usuarios.
  - **config:** Configuraciones de la aplicación, incluyendo seguridad y JWT.
  - **controller:** Controladores que gestionan las solicitudes HTTP relacionadas con los usuarios.
  - **model:** Modelos de datos y DTOs utilizados en la aplicación.
  - **service:** Servicios que contienen la lógica de negocio.
  - **utilities:** Utilidades y helpers para diversas funcionalidades.

- **Características Principales del Módulo**
  - Registro y Autenticación: Permite a los usuarios registrarse, iniciar sesión y gestionar sus perfiles mediante endpoints seguros y autenticación JWT.
  - Verificación de Email: Verifica si una dirección de correo electrónico ya está registrada en el sistema.
  - Gestión de Perfiles: Los usuarios pueden actualizar su información de perfil, como nombre, correo electrónico, y más.
  - Gestión de Favoritos: Permite a los usuarios añadir y gestionar recetas e ingredientes favoritos.


### Módulo de Recetas (recipes-app)

- **linkia.dam.whatacookrecipes:** Contiene las clases principales de la aplicación de recetas.
  - **config:** Configuraciones de la aplicación, incluyendo MongoDB.
  - **controller:** Controladores que gestionan las solicitudes HTTP relacionadas con las recetas.
  - **model:** Modelos de datos y DTOs utilizados en la aplicación.
  - **service:** Servicios que contienen la lógica de negocio.
  - **utilities:** Utilidades para la paginación y ordenación.
  - **service.components:** Componentes que gestionan la creación y manipulación de recetas e ingredientes.
 
- **Características Principales del Módulo**
  - CRUD de Recetas: Permite crear, leer, actualizar y eliminar recetas. Cada receta incluye información detallada como nombre, descripción, ingredientes y categorías.
  - Gestión de Ingredientes: Permite gestionar ingredientes necesarios para las recetas, incluyendo su creación, actualización y eliminación.
  - Gestión de Categorías: Facilita la organización de recetas en categorías, permitiendo su creación, actualización y eliminación.
  - Búsqueda Avanzada: Ofrece funcionalidades de búsqueda avanzada para encontrar recetas basadas en criterios como nombre, ingredientes y categorías, optimizando los resultados para ser precisos y relevantes.


## Instalación y Ejecución

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno local:

### Prerrequisitos

- Docker

### Instrucciones

Para probar la aplicación WhataCook usando Docker, puedes construir y ejecutar un contenedor siguiendo estos pasos:

#### Construir la imagen con **Docker Compose**
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

    
#### Construir la imagen de cada modulo por separdo con **Docker**

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
