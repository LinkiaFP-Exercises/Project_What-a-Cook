
# WhataCook APP

## 1. Introducción

**WhataCook** es una aplicación diseñada para facilitar la planificación de comidas y reducir el desperdicio de alimentos, proporcionando recetas basadas en los ingredientes disponibles en la cocina de los usuarios. A través de una interfaz intuitiva, WhataCook permite a los usuarios introducir los ingredientes que tienen a mano, ofreciendo recetas específicas que pueden ser preparadas con ellos.

## 2. Descripción del Problema

El desperdicio de alimentos es un problema creciente, exacerbado por la compra excesiva y la falta de planificación. Muchos encuentran difícil utilizar todos los ingredientes disponibles en casa, lo que contribuye al desperdicio y gasto innecesario. WhataCook surge como una solución práctica para mejorar la gestión de alimentos en los hogares.

## 3. Solución Propuesta

WhataCook ofrece una herramienta intuitiva para ingresar ingredientes disponibles y sugerir recetas acordes. La aplicación permite una experiencia personalizada, dando la opción de guardar preferencias y recetas favoritas. A través de WhataCook, buscamos proporcionar una solución eficiente para planificar comidas y reducir el desperdicio de alimentos.

## 4. Modelo de Negocio

WhataCook considera varias fuentes de ingresos, incluyendo publicidad, suscripciones premium para acceso a recetas exclusivas y colaboraciones con marcas de alimentos o supermercados.

## 5. Análisis de Mercado

Nuestro análisis destaca una demanda existente en el mercado de aplicaciones de planificación de comidas y recetas, y examina la competencia actual.

## 6. Público Objetivo

Dirigido a personas de todas las edades y niveles de habilidad en la cocina, enfocándonos en aquellos que buscan soluciones prácticas para planificar sus comidas diarias.

## 7. Tecnologías Utilizadas

- **Frontend**: Figma, React Native, Node.js.
- **Backend**: Spring Boot, MongoDB, Swagger, Gradle, JUnit, Mockito, Logback.
- **Comunes**: GitHub, Docker.

## 8. Plan de Implementación

- **11 de marzo de 2024:** Entrega de la previa de la aplicación con al menos el CRUD del login para evaluación.
- **15 de abril de 2024:** Entrega de una previa general de la aplicación con sus funcionalidades básicas y diseño general del frontend. 
- **31 de mayo de 2024:** ENTREGA FINAL - proyecto finalizado y documentado
- **14 de junio de 2024:** Defensa del Proyecto: Presentación que incluye la exposición del trabajo realizado, los resultados obtenidos y las conclusiones alcanzadas. 

## 9. Testear con Docker

Para probar la aplicación WhataCook usando Docker, puedes construir y ejecutar un contenedor siguiendo estos pasos:

### Construir la imagen con Docker Compose
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

    
### Construir la imagen por separdo con Docker

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

Para más detalles sobre cómo ejecutar el contenedor y acceder a la aplicación, te recomendamos consultar la documentación oficial de Docker.

## 10. Postman requests

Puedes encontrar la colección de requests con detalles de como utilizar cada uno de los endpoints en el seguiente enlace:
[WhataCook - @Cookers-NewTests](https://documenter.getpostman.com/view/12946439/2sA35A8QuD)
