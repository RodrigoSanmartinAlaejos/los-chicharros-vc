# Backend: API de Los Chicharros V.C.

API REST con Spring Boot 4.2 y Java 25. Las reglas de código están en
[docs/07-desarrollo/reglas-tecnicas.md](../docs/07-desarrollo/reglas-tecnicas.md) y la forma de
probar en [docs/07-desarrollo/guia-tests.md](../docs/07-desarrollo/guia-tests.md).

## Requisitos

- JDK 25
- Docker en marcha (los tests de integración levantan MySQL con Testcontainers)

## Comandos

En Windows se usa `.\mvnw.cmd` en lugar de `./mvnw`.

| Comando | Qué hace |
| --- | --- |
| `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev` | Arranca la API en local (antes, `docker compose up -d` en la raíz) |
| `./mvnw test` | Tests unitarios |
| `./mvnw verify` | Tests unitarios y de integración, cobertura (JaCoCo, informe en `target/site/jacoco/index.html`), formato (Spotless) y Javadoc de los servicios (Checkstyle); es lo que ejecuta la CI |
| `./mvnw spotless:apply` | Formatea todo el código Java con google-java-format |
| `./mvnw spotless:check` | Solo comprueba el formato |
| `./mvnw checkstyle:check` | Solo comprueba el Javadoc de los servicios |

## Formato y Javadoc

- **Spotless + google-java-format** (2 espacios, líneas de 100 caracteres, imports ordenados).
  `./mvnw verify` falla si un fichero no está formateado; se arregla con `./mvnw spotless:apply`.
- **Checkstyle** (`checkstyle.xml`) solo mira las clases `*Service`: la clase y sus métodos
  públicos necesitan Javadoc. Los constructores, los controladores, los repositorios y los DTOs no.
