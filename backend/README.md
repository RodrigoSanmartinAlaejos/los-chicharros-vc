# Backend: API de Los Chicharros V.C.

API REST con Spring Boot 4.2 y Java 25. Las reglas de código están en
[docs/07-desarrollo/reglas-tecnicas.md](../docs/07-desarrollo/reglas-tecnicas.md) y la forma de
probar en [docs/07-desarrollo/guia-tests.md](../docs/07-desarrollo/guia-tests.md).

## Requisitos

- JDK 25
- Docker en marcha (los tests de integración levantan MySQL con Testcontainers)

## Sesión y administrador inicial

La API necesita tres variables más en el `.env` de la raíz (ver `.env.example`):

| Variable | Para qué |
| --- | --- |
| `JWT_SECRET` | Clave con la que se firman los access tokens. Al menos 32 caracteres aleatorios; si falta o es corta, la API no arranca |
| `ADMIN_EMAIL` | Email del administrador inicial |
| `ADMIN_PASSWORD` | Su contraseña, de 12 caracteres o más |

Al arrancar, si no hay ningún administrador, la API lo crea con esos datos (contraseña cifrada con BCrypt). Si ya existe, no lo toca: cambiar las variables después no cambia la contraseña.

Para generar el secreto en PowerShell:

```powershell
$b = New-Object byte[] 48; [Security.Cryptography.RandomNumberGenerator]::Create().GetBytes($b); [Convert]::ToBase64String($b)
```

O en Linux y macOS: `openssl rand -base64 48`.

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
