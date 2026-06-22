# VetNova - Microservicio de Autenticación (ms-autenticacion)

## Descripción del Proyecto
Este repositorio contiene el microservicio central de seguridad para el sistema VetNova. Su responsabilidad principal es gestionar la identidad de los usuarios, encriptar credenciales y emitir tokens JWT para proteger las rutas de la API bajo una arquitectura Stateless.

## Stack Tecnológico
- Java 21
- Spring Boot 3.2.0
- Spring Security & JWT (JSON Web Tokens)
- Spring Data JPA
- MySQL
- Swagger / OpenAPI

## Configuración y Despliegue
Para evaluar este microservicio de forma local:
1. El servicio está configurado para inicializarse en el puerto `8081`.
2. No es necesario ejecutar scripts SQL manuales. Spring Boot creará automáticamente la base de datos `db_vetnova_auth` y sus tablas al ejecutar la aplicación.

## Endpoints (API REST)
La API expone las siguientes rutas bajo el prefijo `/auth`:

| Método | Ruta | Descripción | Seguridad |
|---|---|---|---|
| POST | `/auth/registro` | Registra un nuevo usuario (valida duplicados y campos en blanco). | Público |
| POST | `/auth/login` | Autentica credenciales y retorna el token JWT. | Público |
| GET | `/auth/lista` | Retorna el listado de usuarios registrados y sus roles. | Requiere Token JWT |
| DELETE | `/auth/eliminar/{id}` | Elimina un usuario por su ID. | Requiere Token JWT |

## Pruebas y Criterios de Aceptación
El proyecto incluye pruebas unitarias implementadas con **JUnit y Mockito**. Estas pruebas están diseñadas para cubrir los criterios de aceptación de las Historias de Usuario (BDD), validando escenarios como:
* Generación correcta de usuarios (Status 201).
* Prevención de registros duplicados.
* Manejo de excepciones y bloqueo por campos inválidos mediante `@Valid` y `GlobalExceptionHandler` (Status 400).

Para las pruebas manuales de integración, la documentación interactiva de Swagger está disponible al levantar el proyecto en:
`http://localhost:8081/swagger-ui.html`