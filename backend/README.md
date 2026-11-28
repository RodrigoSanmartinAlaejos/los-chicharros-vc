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

## Imágenes

Las imágenes de noticias, personas y equipos (H14) se guardan donde diga `IMAGE_STORAGE`:

| Valor | Dónde | Cuándo |
| --- | --- | --- |
| `local` | Carpeta `backend/uploads/` (fuera de Git), servida por la API en `/api/images/**` | `dev`, por defecto |
| `cloudinary` | Cloudinary, en la carpeta `los-chicharros/<entorno>` | `staging` y `prod`, por defecto |

Con `cloudinary` hace falta `CLOUDINARY_URL`, que se copia de «API environment variable» en el panel de Cloudinary (`cloudinary://<api_key>:<api_secret>@<cloud_name>`). Si falta o no tiene esa forma, la API no arranca y dice por qué, sin mostrar el secreto.

Para probar Cloudinary en local: pon `IMAGE_STORAGE=cloudinary` en el `.env`, arranca la API y sube una imagen desde el panel. Debe aparecer en la carpeta `los-chicharros/dev` de la Media Library, y al cambiarla, la anterior desaparece. Vuelve a `IMAGE_STORAGE=local` al terminar.

## Documentación de la API

Con la API arrancada, la documentación interactiva está en <http://localhost:8080/swagger-ui> y el
JSON de OpenAPI en <http://localhost:8080/v3/api-docs>. Se genera desde el código con springdoc y
solo muestra las rutas de `/api/v1`; `OpenApiIT` comprueba que coincide con el contrato
[docs/05-api/openapi-fase-1.yaml](../docs/05-api/openapi-fase-1.yaml).

Para probar una ruta con candado: `POST /api/v1/auth/login` con el administrador del `.env`,
copiar en **Authorize** solo el valor de `accessToken` (el texto que empieza por `eyJ`, sin
comillas ni llaves; Swagger ya añade `Bearer`) y usar **Try it out** (el token dura 15 minutos). En `prod` no hay **Try it out**: solo se lee.

## Comandos

En Windows se usa `.\mvnw.cmd` en lugar de `./mvnw`, y en PowerShell los argumentos `-D` van entre comillas (`.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"`): sin ellas PowerShell los corta por el punto.

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
