# Diccionario de datos de la fase 1

Última revisión: 2026-11-28.

Cada tabla y columna de la fase 1, con su tipo, si admite nulos y sus reglas. Es la base de las migraciones Flyway: si una migración cambia algo de aquí, el diccionario se actualiza en el mismo PR. El diagrama está en el [modelo entidad-relación](modelo-er.md) y las convenciones de nombres en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md).

## Convenciones comunes

- Todas las tablas: `ENGINE=InnoDB`, `CHARSET utf8mb4`, `COLLATE utf8mb4_0900_ai_ci`.
- Clave primaria: `id BIGINT UNSIGNED AUTO_INCREMENT`.
- Fechas con hora en `DATETIME(6)` y en UTC; fechas sin hora en `DATE`.
- Estados y tipos en `VARCHAR(30)` con un `CHECK` de valores permitidos, no `ENUM`.
- Toda clave foránea tiene su índice y `ON DELETE RESTRICT`, salvo que se diga otra cosa.

**Columnas de auditoría.** Todas las tablas de este documento menos `refresh_tokens` llevan además estas cuatro, que rellena Spring Data y no se repiten en cada tabla:

| Columna | Tipo | Nulo | Significado |
| --- | --- | --- | --- |
| `created_at` | `DATETIME(6)` | No | Cuándo se creó la fila |
| `updated_at` | `DATETIME(6)` | No | Cuándo se modificó por última vez |
| `created_by` | `BIGINT UNSIGNED` | Sí | Usuario que la creó (FK `users`); nulo si la creó el sistema |
| `updated_by` | `BIGINT UNSIGNED` | Sí | Usuario que la modificó por última vez (FK `users`) |

## `seasons` (temporadas)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `name` | `VARCHAR(9)` | No | Único (`uk_seasons_name`); formato `2026-27` | Nombre de la temporada |
| `start_date` | `DATE` | No | | Primer día |
| `end_date` | `DATE` | No | `ck_seasons_dates`: posterior a `start_date` | Último día |
| `active` | `BOOLEAN` | No | Por defecto `false`; solo una activa (lo controla el servicio) | Temporada que muestra la web |

## `categories` (categorías)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `name` | `VARCHAR(30)` | No | | Alevín, infantil, cadete, juvenil o sénior |
| `gender` | `VARCHAR(30)` | No | `ck_categories_gender`: `MALE`, `FEMALE`, `MIXED` | Masculina, femenina o mixta |
| `sort_order` | `SMALLINT` | No | | Orden en la web, de menor a mayor edad |

Único: (`name`, `gender`) en `uk_categories_name_gender`.

## `venues` (instalaciones)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `name` | `VARCHAR(100)` | No | Único (`uk_venues_name`) | Nombre del pabellón |
| `address` | `VARCHAR(200)` | No | | Dirección |
| `latitude` | `DECIMAL(9,6)` | Sí | Entre -90 y 90 | Coordenada para el mapa (fase 4) |
| `longitude` | `DECIMAL(9,6)` | Sí | Entre -180 y 180 | Coordenada para el mapa (fase 4) |

## `teams` (equipos)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `season_id` | `BIGINT UNSIGNED` | No | FK `seasons` | Temporada |
| `category_id` | `BIGINT UNSIGNED` | No | FK `categories` | Categoría |
| `name` | `VARCHAR(100)` | No | | Nombre del equipo |
| `own_club` | `BOOLEAN` | No | Por defecto `false` | `true` si es del club; `false` si es rival |
| `crest_url` | `VARCHAR(500)` | Sí | | URL del escudo (rivales) o de la foto del equipo |

Único: (`season_id`, `category_id`, `name`) en `uk_teams_season_category_name`.

## `people` (personas)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `first_name` | `VARCHAR(60)` | No | | Nombre |
| `last_name` | `VARCHAR(100)` | No | | Apellidos; en público solo se muestra la inicial |
| `birth_date` | `DATE` | Sí | | Fecha de nacimiento; nunca se muestra en público |
| `photo_url` | `VARCHAR(500)` | Sí | | URL de la foto |
| `image_consent` | `BOOLEAN` | No | Por defecto `false` | Si se puede mostrar la foto |
| `deleted_at` | `DATETIME(6)` | Sí | | Borrado lógico; las consultas normales excluyen las filas con valor |

Índice: `idx_people_last_name_first_name` para el buscador del panel.

## `team_memberships` (pertenencias)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `team_id` | `BIGINT UNSIGNED` | No | FK `teams` | Equipo |
| `person_id` | `BIGINT UNSIGNED` | No | FK `people` | Persona |
| `type` | `VARCHAR(30)` | No | `ck_team_memberships_type`: `PLAYER`, `COACH` | Jugador o entrenador |
| `shirt_number` | `TINYINT UNSIGNED` | Sí | `ck_team_memberships_shirt_number`: entre 0 y 99; solo jugadores | Dorsal |
| `position` | `VARCHAR(30)` | Sí | Solo jugadores | Posición (colocador, opuesto, central, receptor, líbero) |

Únicos: (`team_id`, `person_id`, `type`) en `uk_team_memberships_team_person_type` y (`team_id`, `shirt_number`) en `uk_team_memberships_team_shirt` (los nulos no chocan). `ck_team_memberships_coach_without_shirt`: un entrenador no tiene dorsal ni posición (desde el #61).

Al borrar una persona (borrado lógico), sale de las plantillas de las temporadas que aún no han terminado (`seasons.end_date` igual o posterior a hoy), para que su dorsal quede libre; en las de temporadas pasadas se conserva. Al borrar un equipo se borra su plantilla.

## `competitions` (competiciones)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `season_id` | `BIGINT UNSIGNED` | No | FK `seasons` | Temporada |
| `category_id` | `BIGINT UNSIGNED` | No | FK `categories` | Categoría |
| `name` | `VARCHAR(100)` | No | | Nombre de la liga |
| `federation` | `VARCHAR(100)` | Sí | | Federación que la organiza |
| `forfeit_penalty_points` | `TINYINT UNSIGNED` | No | Por defecto 1; `ck_competitions_forfeit_penalty`: 10 como máximo | Puntos que se restan por incomparecencia |

Único: (`season_id`, `category_id`, `name`) en `uk_competitions_season_category_name` (desde el #62). Al borrar una competición se borran sus jornadas y su clasificación; si alguna jornada tiene partidos, no se puede borrar.

## `matchdays` (jornadas)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `competition_id` | `BIGINT UNSIGNED` | No | FK `competitions`, `ON DELETE CASCADE` | Competición |
| `number` | `SMALLINT UNSIGNED` | No | `ck_matchdays_number`: de 1 a 99 | Número de jornada |
| `scheduled_date` | `DATE` | Sí | Dentro de la temporada de la competición (lo comprueba la API) | Fecha prevista |

Único: (`competition_id`, `number`) en `uk_matchdays_competition_number`.

## `matches` (partidos)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `matchday_id` | `BIGINT UNSIGNED` | No | FK `matchdays` | Jornada |
| `home_team_id` | `BIGINT UNSIGNED` | No | FK `teams` | Equipo local |
| `away_team_id` | `BIGINT UNSIGNED` | No | FK `teams`; `ck_matches_teams`: distinto del local | Equipo visitante |
| `venue_id` | `BIGINT UNSIGNED` | Sí | FK `venues` | Pabellón, si es uno de los del club |
| `starts_at` | `DATETIME(6)` | No | UTC | Fecha y hora de inicio |
| `status` | `VARCHAR(30)` | No | `ck_matches_status`: `SCHEDULED`, `IN_PROGRESS`, `FINISHED`, `POSTPONED`, `CANCELLED`, `FORFEITED` | Estado |
| `home_sets` | `TINYINT UNSIGNED` | Sí | `ck_matches_sets`: si hay marcador, uno llega a 3 y el otro a menos de 3 | Sets del local |
| `away_sets` | `TINYINT UNSIGNED` | Sí | | Sets del visitante |

Índice: `idx_matches_starts_at` para el calendario. `forfeited_team_id` se añade en la fase 3.

Desde el #63, la API comprueba además que los dos equipos son de la temporada y la categoría de la competición, que ninguno juega otro partido en la misma jornada (409) y que el partido cae dentro de la temporada, contando los días en la hora de Canarias. Mientras un partido exista, no se pueden borrar su jornada, su competición, sus equipos ni su pabellón (409).

## `standings` (clasificación)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `competition_id` | `BIGINT UNSIGNED` | No | FK `competitions`, `ON DELETE CASCADE` | Competición |
| `team_id` | `BIGINT UNSIGNED` | No | FK `teams` | Equipo |
| `points` | `SMALLINT` | No | Por defecto 0 | Puntos (puede ser negativo por sanciones) |
| `played` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Partidos jugados |
| `won` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Ganados |
| `lost` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Perdidos |
| `sets_won` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Sets a favor |
| `sets_lost` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Sets en contra |
| `penalty_points` | `SMALLINT UNSIGNED` | No | Por defecto 0 | Puntos de sanción acumulados |

Único: (`competition_id`, `team_id`) en `uk_standings_competition_team`. En la fase 1 se edita a mano; desde la fase 3 la recalcula el servicio.

Desde el #65, la API comprueba además que cada equipo es de la temporada y la categoría de la competición y que los ganados y los perdidos suman los jugados (en voleibol no hay empates). Mientras un equipo tenga filas de clasificación no se puede borrar (409); borrar la competición borra su clasificación.

## `news_articles` (noticias)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `title` | `VARCHAR(150)` | No | | Título |
| `slug` | `VARCHAR(170)` | No | Único (`uk_news_articles_slug`) | Parte de la URL, generada del título |
| `summary` | `VARCHAR(300)` | No | | Resumen para listados y vista previa al compartir |
| `content` | `MEDIUMTEXT` | No | Markdown | Cuerpo; se convierte y sanea al devolverlo |
| `image_url` | `VARCHAR(500)` | Sí | | Imagen principal |
| `type` | `VARCHAR(30)` | No | `ck_news_articles_type`: `MATCH_REPORT`, `EVENT`, `STATEMENT` | Crónica, evento o comunicado |
| `featured` | `BOOLEAN` | No | Por defecto `false` | Destacada en la portada |
| `published_at` | `DATETIME(6)` | Sí | Nulo = borrador | Cuándo se publicó |
| `deleted_at` | `DATETIME(6)` | Sí | | Borrado lógico |

Índice: `idx_news_articles_published_at`.

Desde el #56: una noticia con `published_at` futuro está programada y no se ve en la web hasta esa fecha. El slug no se reutiliza nunca, ni el de una noticia borrada. El cuerpo admite hasta 50 000 caracteres (lo comprueba la API).

## `users` (usuarios)

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `email` | `VARCHAR(254)` | No | Único (`uk_users_email`), guardado en minúsculas | Email de acceso |
| `password_hash` | `VARCHAR(100)` | No | BCrypt | Contraseña cifrada; nunca sale de la API |
| `active` | `BOOLEAN` | No | Por defecto `true` | Si puede iniciar sesión |
| `deleted_at` | `DATETIME(6)` | Sí | | Borrado lógico |

En la fase 1 solo existe el administrador, creado al arrancar desde `ADMIN_EMAIL` y `ADMIN_PASSWORD`. En la fase 2 se añaden `person_id` y la tabla de roles.

## `refresh_tokens` (tokens de refresco)

Sin columnas de auditoría: tiene sus propias fechas.

| Columna | Tipo | Nulo | Reglas | Significado |
| --- | --- | --- | --- | --- |
| `user_id` | `BIGINT UNSIGNED` | No | FK `users`, `ON DELETE CASCADE` | Dueño de la sesión |
| `token_hash` | `CHAR(64)` | No | Único (`uk_refresh_tokens_token_hash`) | SHA-256 del token; el token nunca se guarda |
| `created_at` | `DATETIME(6)` | No | | Cuándo se emitió |
| `expires_at` | `DATETIME(6)` | No | | Caducidad: 2 horas desde su emisión |
| `session_expires_at` | `DATETIME(6)` | No | | Fin de la sesión: 7 días desde el login; se hereda al rotar |
| `revoked_at` | `DATETIME(6)` | Sí | | Cuándo se usó o se revocó; si llega uno revocado, se revocan todos los del usuario |

## Orden de las migraciones

Cada tabla en su migración, en este orden para respetar las claves foráneas:

| Migración | Tabla |
| --- | --- |
| `V1__create_users.sql` | `users` (primero, porque las demás apuntan a ella con `created_by`) |
| `V2__create_refresh_tokens.sql` | `refresh_tokens` |
| `V3__create_seasons.sql` | `seasons` |
| `V4__create_categories.sql` | `categories` |
| `V4_1__insert_categories.sql` | Las diez categorías del club (catálogo, también en producción) |
| `V5__create_venues.sql` | `venues` |
| `V6__create_people.sql` | `people` (desde el #60) |
| `V7__create_teams.sql` | `teams` |
| `V8__create_team_memberships.sql` | `team_memberships` |
| `V9__create_competitions.sql` | `competitions` |
| `V10__create_matchdays.sql` | `matchdays` |
| `V11__create_matches.sql` | `matches` |
| `V12__create_standings.sql` | `standings` |
| `V13__create_news_articles.sql` | `news_articles` |

`people` va antes que `teams` porque su historia (H16) se hizo antes; ninguna de las dos depende de la otra, y `team_memberships` apunta a las dos.

Los datos de ejemplo de `dev` y `staging` no son migraciones versionadas: van en `db/seed` como migraciones repetibles (reglas técnicas, regla 4 de bases de datos). La primera es `R__seed_01_configuration.sql`: la temporada 2026-27 activa y los dos pabellones; las demás las genera `scripts/datos-ejemplo/semillas.mjs` (personas, equipos y, desde el #62, competiciones y jornadas en `R__seed_04_competitions.sql` y, desde el #63, partidos en `R__seed_05_matches.sql` y, desde el #65, la clasificación en `R__seed_06_standings.sql` y, desde el #56, las noticias en `R__seed_07_news.sql`).

## Decisiones

Decisiones tomadas al redactar el diccionario que no estaban escritas en el plan; confirmadas el 2026-11-22:

- **Categoría con sexo:** `categories` guarda la categoría y el sexo juntos («Cadete femenina»), y el orden en la web.
- **Pabellón opcional** en los partidos fuera de casa, y rivales en la tabla `teams` con `own_club = false`.
- **Fecha de nacimiento opcional:** los entrenadores adultos no necesitan tenerla; para los jugadores se pedirá en el formulario.
- **Tamaños de los textos:** título de noticia de 150 caracteres, resumen de 300 y nombres de 60 + 100.
- **Puntos negativos** permitidos en la clasificación, por las sanciones.
