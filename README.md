# Modelo de Dominio – Sistema de Triage Académico

El siguiente diagrama UML representa el modelo de dominio del sistema, incluyendo las entidades principales, sus atributos y las relaciones entre ellas.

<img width="2061" height="2186" alt="Diagrama_final" src="https://github.com/user-attachments/assets/02d6d120-ad4d-460d-9759-c9bc81f3a20c" />

Sistema de Triage de Solicitudes Académicas

Descripción:

Sistema backend desarrollado en Spring Boot para la gestión de solicitudes académicas. Permite registrar, clasificar, priorizar, asignar responsables y gestionar el ciclo de vida completo de cada solicitud, manteniendo un historial auditable.

Funcionalidades principales:

-Registro de solicitudes académicas
-Clasificación de solicitudes
-Priorización basada en reglas de negocio
-Asignación de responsables
-Gestión del ciclo de vida: REGISTRADA, CLASIFICADA, EN_ATENCION, ATENDIDA, CERRADA
-Historial auditable de cada solicitud
-Consulta de solicitudes con filtros
-API REST documentada con Swagger (OpenAPI)
-Sugerencia de clasificación (IA básica simulada)

Tecnologías utilizadas:
-Java 21
-Spring Boot
-Spring Data JPA (Hibernate)
-MariaDB
-Gradle
-Swagger / OpenAPI

Cómo ejecutar el proyecto:

Clonar el repositorio
Configurar la base de datos en application.properties:

spring.datasource.url=jdbc:mariadb://localhost:3306/triage
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_CONTRASEÑA

Ejecutar el proyecto:

./gradlew bootRun

Acceder a Swagger:

http://localhost:8080/swagger-ui/index.html

Ejemplo de uso:

POST /api/solicitudes

{
"tipo": "CONSULTA_ACADEMICA",
"descripcion": "Necesito información sobre homologación de asignaturas.",
"canalOrigen": "SAC",
"solicitanteId": 1
}

Códigos de respuesta importantes
200 → Operación exitosa
201 → Recurso creado
400 → Error de validación
403 → No autorizado
404 → Recurso no encontrado
409 → Conflicto (transición inválida)
422 → Regla de negocio

Notas:
-El sistema cumple con los requisitos funcionales del Hito 2
-La IA implementada es simulada (no depende de servicios externos)
-Arquitectura basada en capas (Controller, Service, Repository)

Ingeniería de Sistemas y Computación
Universidad del Quindío