# API REST

Última revisión: 2026-11-26.

Convenciones de la API y contrato de la fase 1. Es el entregable del paso 9 de la planificación. El contrato completo, con cada ruta, sus parámetros, sus DTOs y sus errores, está en [`openapi-fase-1.yaml`](openapi-fase-1.yaml) (OpenAPI 3.1). Se puede abrir en [Swagger Editor](https://editor.swagger.io) pegando el fichero, o con la extensión *OpenAPI (Swagger) Editor* de VS Code. Desde el #90 (T08), la API genera la misma documentación a partir del código con springdoc, en `/swagger-ui` (JSON en `/v3/api-docs`), con solo las rutas que ya existen; `OpenApiIT` comprueba que coinciden con este contrato en etiquetas, resúmenes, seguridad y códigos de respuesta. En `prod` es solo de lectura.

## Convenciones

Resumen de las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md), que son las que mandan:

- Todas las rutas cuelgan de `/api/v1`, en inglés, en plural y en `kebab-case`. El navegador siempre llama a través del proxy del SSR ([ADR-004](../adr/004-ssr-api-proxy.md)).
- JSON en `camelCase`; fechas con hora en ISO 8601 y UTC (`2026-11-14T17:30:00Z`); fechas sin hora como `2026-11-14`.
- Paginación con `?page=0&size=20&sort=publishedAt,desc`; tamaño máximo 100. La respuesta trae `content`, `page`, `size`, `totalElements` y `totalPages`.
- Lectura pública y gestión comparten ruta; quién puede escribir lo decide la [matriz de permisos](../02-requisitos/matriz-permisos.md). En la fase 1 solo escribe el administrador. Desde #53 la escritura exige el access token en `Authorization: Bearer`; sin token, o con uno caducado o falso, la respuesta es 401, y con sesión pero sin el rol, 403. Bajo `/api/v1` toda ruta exige sesión salvo el login y las lecturas públicas.
- Las respuestas públicas muestran las personas como `displayName` («Lucía M.») y la foto solo con consentimiento de imagen.

## Métodos y códigos

| Operación | Método | Éxito | Errores habituales |
| --- | --- | --- | --- |
| Listar | `GET /recursos` | 200 | 400 |
| Obtener uno | `GET /recursos/{id}` | 200 | 404 |
| Crear | `POST /recursos` | 201 + cabecera `Location` | 400, 409 |
| Sustituir | `PUT /recursos/{id}` | 200 | 400, 404, 409 |
| Modificar en parte | `PATCH /recursos/{id}` | 200 | 400, 404 |
| Borrar | `DELETE /recursos/{id}` | 204 | 404, 409 si tiene dependencias |
| Subir imagen | `PUT /recursos/{id}/image` (multipart) | 200 | 400, 404, 413 |
| Sin sesión o sin permiso | cualquiera | — | 401, 403, 429 |

## Errores

Todos los errores siguen RFC 7807 (`application/problem+json`). En los de validación, `errors` detalla cada campo:

```json
{
  "type": "about:blank",
  "title": "Bad Request",
  "status": 400,
  "detail": "La petición tiene campos no válidos",
  "instance": "/api/v1/news",
  "errors": [
    { "field": "title", "message": "no debe estar vacío" }
  ]
}
```

El login fallido devuelve siempre el mismo 401, exista o no el email y esté o no bloqueado, para no dar pistas.

## Rutas de la fase 1

| Área | Rutas |
| --- | --- |
| Autenticación | `POST /auth/login`, `/auth/refresh`, `/auth/logout` |
| Noticias | `GET, POST /news` · `GET /news/by-slug/{slug}` · `GET, PUT, PATCH, DELETE /news/{id}` · `PUT /news/{id}/image` |
| Equipos | `GET, POST /teams` · `GET, PUT, DELETE /teams/{id}` · `PUT /teams/{id}/crest` · `POST /teams/{id}/roster` · `PATCH, DELETE /teams/{id}/roster/{membershipId}` |
| Personas | `GET, POST /people` · `GET, PUT, DELETE /people/{id}` · `PUT /people/{id}/photo` |
| Competiciones | `GET, POST /competitions` · `GET, PUT, DELETE /competitions/{id}` · `GET, PUT /competitions/{id}/standings` · `GET /competitions/{id}/matchdays` · `POST /matchdays` · `PUT, DELETE /matchdays/{id}` |
| Partidos | `GET, POST /matches` · `GET, PUT, PATCH, DELETE /matches/{id}` |
| Configuración | `GET, POST /seasons`, `/categories`, `/venues` · `PUT, DELETE` sobre cada `{id}` |

Fuera de `/api/v1` queda `/actuator/health`, que solo devuelve el estado.

## Decisiones

Decisiones tomadas al escribir el contrato que cambian o completan el [plan](../00-plan/plan-proyecto.md); confirmadas el 2026-11-23 y ya llevadas al plan:

- **`GET /news/by-slug/{slug}` en lugar de `GET /news/{slug}`.** OpenAPI no permite dos rutas iguales con distinto parámetro (`/news/{slug}` para leer y `/news/{id}` para editar), y editar por slug es frágil porque el slug cambia si cambia el título. La URL de la web sigue siendo `/noticias/{slug}`.
- **Rutas que el plan no listaba:** `GET /news/{id}` (el panel edita borradores), `GET /competitions` (para elegir liga en la clasificación) y `GET /matches/{id}`.
- **`GET /matches` paginado**, con filtros por equipo, pabellón, fechas y estado.
- **Tamaño de página por defecto: 20**; las noticias de la web piden 10 (H02).
- **Imágenes JPG, PNG o WebP de 5 MB como máximo**; 413 si se pasa.
