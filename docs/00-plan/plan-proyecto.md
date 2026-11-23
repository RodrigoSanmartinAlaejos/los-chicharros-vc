# Plan de proyecto: Web del club de voleibol

Última revisión: 2026-11-22.

## Visión general

El objetivo es crear la web completa de un club de voleibol ficticio, **Los Chicharros V.C.** (nombre provisional): pública para aficionados y familias, y con áreas privadas para socios, entrenadores y administración. Es un proyecto personal para mi portfolio fullstack. Si más adelante lo presento a un club real, adaptaré nombre, datos y requisitos.

**Alcance:** noticias, equipos por categoría, calendario de partidos, clasificaciones, área de socios, área de entrenadores y panel de administración. Fuera de alcance por ahora: app móvil nativa, streaming de partidos y tienda online.

### Roles

| Rol                   | Qué puede hacer                                                                                                       |
|-----------------------|-----------------------------------------------------------------------------------------------------------------------|
| Visitante             | Ver noticias, equipos, calendario, clasificaciones y contacto                                                         |
| Socio / jugador       | Ver su perfil, sus equipos, convocatorias, estado de cuota y avisos privados                                          |
| Tutor                 | Padre, madre o tutor legal: gestiona a sus hijos menores, responde convocatorias y paga sus cuotas                    |
| Entrenador            | Gestionar sus equipos, convocar, pasar asistencia e introducir resultados                                             |
| Coordinador deportivo | Supervisar todos los equipos y entrenadores, gestionar calendario, entrenamientos y resultados de cualquier categoría |
| Comunicación          | Publicar y editar noticias, fotos y avisos generales del club                                                         |
| Tesorero              | Gestionar cuotas y pagos, enviar recordatorios y exportar informes económicos; sin acceso a la gestión deportiva      |
| Administrador         | Acceso total: usuarios, roles, temporadas, categorías, instalaciones y configuración                                  |

Un usuario puede tener varios roles a la vez, por ejemplo un entrenador que también es socio. El detalle de qué puede hacer cada rol está en la matriz de permisos ([matriz-permisos.md](../02-requisitos/matriz-permisos.md)); las tablas de endpoints de este plan resumen el acceso, y si hay diferencia manda la matriz. En la fase 1 solo existe el administrador; el resto de roles se implementa en la fase 2.

### Stack tecnológico

| Capa              | Tecnología                                                                                                                 |
|-------------------|----------------------------------------------------------------------------------------------------------------------------|
| Backend           | Java 25 (LTS), Maven, Spring Boot 4.1, Spring Web, Spring Data JPA, Spring Security + JWT                                  |
| Base de datos     | MySQL 8.4, migraciones con Flyway                                                                                          |
| Frontend          | Angular 22 con SSR (renderizado en servidor para la parte pública), Node 24 LTS, pnpm                                      |
| Documentación API | Swagger / OpenAPI (springdoc)                                                                                              |
| Tests             | JUnit 5, Mockito, Testcontainers (MySQL), Vitest, Playwright, k6                                                           |
| Imágenes          | Interfaz `ImageStorage`: disco local en `dev`, Cloudinary en `staging` y `prod`                                            |
| Monitorización    | Spring Boot Actuator, monitor externo de caídas, Sentry                                                                    |
| Infraestructura   | Docker Compose, GitHub y GitHub Actions; Render (API y frontend) y TiDB Cloud Starter (base de datos), en planes gratuitos |

Las versiones exactas y cómo se fijan están en las reglas técnicas.

### Cómo usar este documento

Cada fase tiene un objetivo, lo que incluye, el modelo de datos y endpoints que añade, y una checklist de tareas. Marco las tareas al terminarlas y anoto las decisiones en la sección final. Una fase está terminada cuando cumple su criterio de salida.

**No hay fechas.** El proyecto avanza por orden de fases, en sprints de dos semanas con el objetivo que quepa en 5-10 h semanales. El alcance se controla con el criterio de salida de cada fase y el objetivo de cada sprint, no con un calendario.

## Fase 0: Preparación

**Objetivo:** tener el repositorio con la CI funcionando y la fase 1 diseñada antes de escribir código de negocio.

Tiene dos bloques que pueden avanzar en paralelo.

### Bloque A: repositorio, CI y entorno

- [x] Crear repositorio en GitHub (monorepo con `backend/`, `frontend/` y `docs/`), con README, LICENSE (MIT), `.gitignore` y `.editorconfig`
- [x] Copiar a `docs/` este plan, las pautas, las reglas técnicas, la matriz de permisos y la paleta de colores; cambiar sus rutas claude/... por enlaces relativos de docs/; marcar los documentos vivos como «Versión de planificación; la vigente está en el repositorio» y dejar en las copias del proyecto solo un enlace al repositorio
- [x] Generar el proyecto Spring Boot 4.1 con Java 25 (Spring Initializr)
- [x] Crear el proyecto Angular 22 con SSR, usando pnpm
- [x] Fijar versiones: `pom.xml`, `.nvmrc`, `packageManager` en `package.json`
- [x] `docker-compose.yml` con MySQL 8.4 y Mailpit para desarrollo local
- [x] Perfiles de Spring `dev`, `test` y `prod` (el perfil `staging` queda definido, pero no se despliega hasta cerrar la fase 1)
- [x] CI con GitHub Actions: compilar, tests, formato y linter de backend y frontend en cada push y PR (el formato y el linter del backend, Spotless y Checkstyle, quedan en su propio issue)
- [x] Protección de ramas `main` y `develop`, Dependabot y secret scanning
- [x] Plantillas de issue y PR, etiquetas, milestones y tablero de GitHub Projects
- [x] ADR-001 (Angular), ADR-002 (Angular SSR) y ADR-003 (separación `Person` / `User`)
- [x] ADR-004 (proxy de la API en el servidor SSR, porque `onrender.com` está en la Public Suffix List)

### Bloque B: planificación de la fase 1

Son los 12 pasos de la fase de planificación de las pautas, detallados **solo para la fase 1**. Las fases 2 a 5 se quedan a alto nivel y se detallan al llegar a cada una.

- [x] 1. Visión y alcance
- [x] 2. Club ficticio y glosario: ficha del club con escudo, categorías y competiciones de ejemplo, y glosario
- [x] 3. Roles y permisos: matriz de permisos (primera versión, 2026-11-10)
- [x] 4. Casos de uso por rol
- [x] 5. Historias de usuario de la fase 1 con criterios de aceptación y prioridad MoSCoW
- [x] 6. Requisitos no funcionales
- [x] 7. Modelo de datos: diagrama ER completo a alto nivel y diccionario de datos de la fase 1
- [x] 8. Arquitectura: diagramas C4
- [x] 9. Contrato OpenAPI inicial de la fase 1
- [x] 10. Flujos principales de la fase 1 (login del admin, publicar noticia, introducir resultado)
- [x] 11. Wireframes de las pantallas clave y tipografía. La paleta ya está elegida: Atlántico y arena ([paleta-colores.md](../02-requisitos/paleta-colores.md))
- [x] 12. Plan de sprints de la fase 1

Además, para los datos de ejemplo:

- [ ] Preparar noticias, jugadores, rivales, partidos y resultados ficticios (tarea T04, sprint 0)
- [ ] Buscar imágenes libres de derechos para noticias y equipos (tarea T04, sprint 0)

**Criterio de salida:** CI en verde sobre un repositorio en el que backend y frontend arrancan en local contra MySQL en Docker, y la fase 1 tiene historias priorizadas, diagrama ER, contrato de API y wireframes.

## Fase 1: MVP público

**Objetivo:** una web pública desplegada con noticias, equipos, calendario y clasificación, alimentada desde un panel de administración básico. Es la primera versión enseñable.

### Qué incluye

- **Inicio:** últimas noticias, próximos partidos y últimos resultados
- **Noticias:** listado paginado, detalle, tipos (crónica, evento, comunicado) y destacadas; el cuerpo se escribe en Markdown
- **Equipos:** ficha por categoría con plantilla (nombre y dorsal) y entrenadores
- **Calendario:** próximos partidos con filtro por equipo y pabellón
- **Clasificación:** tabla por competición y marcador de sets de cada partido, introducidos a mano
- **Club:** historia, instalaciones, cómo apuntarse y enlaces a redes sociales
- **Login y sesión:** un único administrador entra con email y contraseña, recibe un access token JWT y mantiene la sesión con un refresh token en cookie. La sesión es deslizante: termina tras 2 horas sin actividad o, como máximo, 7 días después del login, y se puede cerrar. Sin roles, invitaciones ni recuperación de contraseña, que llegan en la fase 2
- **Panel de gestión:** CRUD de todo lo anterior en /gestion. En la fase 1 solo entra el administrador; desde la fase 2, cada rol de gestión ve sus secciones y la configuración sigue siendo solo del admin

### Modelo de datos

| Entidad                            | Campos principales                                                                                                | Relaciones                                              |
|------------------------------------|-------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------|
| `Season` (temporada)               | name (2026-27), startDate, endDate, active                                                                        | 1:N Competition, 1:N Team                               |
| `Category` (categoría)             | name (alevín, cadete…), gender                                                                                    | 1:N Team                                                |
| `Team` (equipo)                    | name, ownClub, crestUrl                                                                                           | N:1 Category, N:1 Season                                |
| `Person` (persona)                 | firstName, lastName, birthDate, photoUrl, imageConsent                                                            | Cualquier jugador o entrenador, tenga o no cuenta       |
| `TeamMembership` (pertenencia)     | type (PLAYER o COACH), shirtNumber, position                                                                      | N:1 Person, N:1 Team                                    |
| `Competition` (competición)        | name, federation, forfeitPenaltyPoints                                                                            | N:1 Season, N:1 Category                                |
| `Matchday` (jornada)               | number, scheduledDate                                                                                             | N:1 Competition                                         |
| `Match` (partido)                  | startsAt, status, homeSets, awaySets, forfeitedTeam                                                               | N:1 Matchday, N:1 Venue, homeTeam y awayTeam (N:1 Team) |
| `Venue` (instalación)              | name, address, latitude, longitude                                                                                | 1:N Match                                               |
| `NewsArticle` (noticia)            | title, slug, summary, content (Markdown), imageUrl, type (MATCH_REPORT, EVENT o STATEMENT), featured, publishedAt | —                                                       |
| `Standing` (clasificación)         | team, points, played, won, lost, setsWon, setsLost, penaltyPoints                                                 | N:1 Competition (en la fase 3 se calcula sola)          |
| `User` (usuario)                   | email, passwordHash, active                                                                                       | En esta fase solo existe el administrador               |
| `RefreshToken` (token de refresco) | tokenHash, expiresAt (2 h, se renueva en cada uso), sessionExpiresAt (7 días desde el login), revokedAt           | N:1 User                                                |

`Person` guarda los datos de una persona y `User` es solo la cuenta de acceso, que es opcional: un jugador menor es una `Person` sin `User`. Por eso ya no hay entidad `Player` ni migración de `Player` a `User` en la fase 2.

Estados de un partido (`status`): `SCHEDULED`, `IN_PROGRESS`, `FINISHED`, `POSTPONED`, `CANCELLED` y `FORFEITED` (incomparecencia; `forfeitedTeam` indica quién no se presentó).

### Endpoints REST

| Método                  | Ruta                                                                                                  | Acceso                                               |
|-------------------------|-------------------------------------------------------------------------------------------------------|------------------------------------------------------|
| GET                     | `/api/v1/news?type=&page=`                                                                            | Público                                              |
| GET                     | `/api/v1/news/by-slug/{slug}`                                                                         | Público                                              |
| GET                     | `/api/v1/teams`, `/api/v1/teams/{id}`                                                                 | Público                                              |
| GET                     | `/api/v1/matches?teamId=&venueId=&from=&to=&status=&page=`, `/api/v1/matches/{id}`                    | Público                                              |
| GET                     | `/api/v1/competitions?seasonId=&categoryId=`, `/api/v1/competitions/{id}`                             | Público                                              |
| GET                     | `/api/v1/competitions/{id}/standings`                                                                 | Público                                              |
| GET                     | `/api/v1/competitions/{id}/matchdays`                                                                 | Público                                              |
| POST                    | `/api/v1/auth/login`, `/api/v1/auth/refresh`, `/api/v1/auth/logout`                                   | Público / autenticado                                |
| POST, PUT, DELETE       | `/api/v1/{news, teams, people, competitions, matchdays, matches}`                                     | Admin (desde la fase 2, según la matriz de permisos) |
| GET / POST, PUT, DELETE | `/api/v1/{seasons, categories, venues}`                                                               | Público / Admin                                      |
| PUT                     | `/api/v1/news/{id}/image`, `/api/v1/people/{id}/photo`, `/api/v1/teams/{id}/crest` (multipart)        | Admin                                                |
| POST / PATCH, DELETE    | `/api/v1/teams/{id}/roster` / `/api/v1/teams/{id}/roster/{membershipId}`                              | Admin                                                |
| PUT                     | `/api/v1/competitions/{id}/standings` (desaparece en la fase 3)                                       | Admin                                                |
| GET                     | `/api/v1/people?search=&page=`                                                                        | Admin                                                |
| GET                     | `/api/v1/news?status=draft` (borrador = sin publishedAt; publicar y retirar con PATCH de publishedAt) | Admin (desde la fase 2, también comunicación)        |
| GET                     | `/api/v1/news/{id}` (incluye borradores y el cuerpo en Markdown)                                      | Admin (desde la fase 2, también comunicación)        |
| GET                     | `/actuator/health`                                                                                    | Público (solo estado)                                |

Las rutas de gestión son las del propio recurso, protegidas por rol; `/api/v1/admin/...` queda solo para la configuración que únicamente toca el administrador.

### Tareas

- [ ] Entidades JPA y migraciones Flyway, con `created_by` y `updated_by` en las tablas de negocio (excepciones en las reglas técnicas)
- [ ] Datos semilla (seed) con una temporada de ejemplo
- [ ] Capas controller, service, repository con DTOs (no exponer entidades)
- [ ] Validación con Bean Validation y manejo global de errores (`@RestControllerAdvice`)
- [ ] Login: Spring Security, BCrypt, access token JWT y admin creado desde variables de entorno
- [ ] Límite de intentos de login: 5 fallos seguidos por email + IP bloquean esa combinación 15 minutos, máximo 20 peticiones por minuto por IP en `/api/v1/auth/**` (Bucket4j), contadores en memoria (Caffeine) y respuesta 401 genérica
- [ ] Refresh token en cookie httpOnly con rotación y detección de reutilización, sesión deslizante (2 h sin actividad, 7 días como máximo) y cierre de sesión; access token corto solo en memoria
- [ ] Cookie de refresco sin Domain, servida a través del proxy /api del frontend; CORS desactivado en la API
- [ ] Paginación en noticias
- [ ] Noticias en Markdown: conversión a HTML y saneado en el backend con lista blanca de etiquetas
- [ ] Subida de imágenes con la interfaz `ImageStorage` (disco local en `dev`, Cloudinary en `prod`)
- [ ] Swagger con todos los endpoints
- [ ] Actuator con `/actuator/health` y monitor externo de caídas
- [ ] Frontend con SSR: layout, menú, inicio, noticias, equipos, calendario, clasificación
- [ ] Proxy de `/api` en el servidor SSR (y `proxy.conf.json` en local), con `X-Forwarded-For` y el mismo tamaño máximo de cuerpo que la API
- [ ] Frontend: login, renovación del access token antes de que caduque y panel de admin con formularios CRUD (editor Markdown con vista previa y borrador guardado en el navegador)
- [ ] Diseño responsive desde el principio (móvil primero)
- [ ] Tests: unitarios e integración (Testcontainers) en backend, Vitest en frontend
- [ ] Primer despliegue en Render y TiDB Cloud Starter (`prod`)
- [ ] Workflow de despliegue a producción: se lanza al publicar una etiqueta vX.Y.Z, espera la aprobación manual (GitHub Environments), exporta la base con Dumpling, ejecuta Flyway contra TiDB con el usuario `*_migrator` y, si termina bien, deploy hook de Render (auto-deploy desactivado)
- [ ] Comprobar si TiDB Cloud Starter permite activar `tidb_enable_check_constraint`; si no, `CHECK` solo en Java y ADR
- [ ] Medir el consumo de memoria de la API en Render tras el primer despliegue y ajustar las opciones de la JVM si pasa del 80 % (unos 410 MB)
- [ ] Copias de seguridad de club_prod: exportación Dumpling cifrada con age, semanal y antes de cada release, guardada 30 días como artifact; primera prueba de restauración en local

**Criterio de salida:** la web está online, el admin puede publicar una noticia y un partido, y cualquiera los ve desde el móvil.

## Fase 2: Autenticación y roles

**Objetivo:** que cada persona entre con su cuenta y vea solo lo que le corresponde según su rol.

### Qué incluye

- **Entorno de pruebas:** antes de empezar, `develop` pasa a desplegarse sola en `staging`
- **Cuentas e invitaciones:** el admin da de alta a socios y entrenadores, que reciben una invitación para crear su contraseña
- **Recuperar contraseña** por email con un token de un solo uso
- **Envío de emails** con Brevo: la base que usan la invitación y la recuperación de contraseña, y que en la fase 4 reutilizan las convocatorias y las cuotas
- **Aviso de intentos fallidos:** email al usuario cuando hay varios intentos de login fallidos con su cuenta (el límite de intentos, el refresh token y el cierre de sesión ya existen desde la fase 1)
- **Área de socio:** perfil, mis equipos, mis próximos partidos, avisos privados
- **Área de entrenador:** sus equipos y su plantilla, editar datos de sus jugadores
- **Roles de gestión:** coordinador, comunicación y tesorero, según la matriz de permisos
- **Admin:** gestión de usuarios, asignar roles, asignar entrenadores a equipos
- **Tutores legales:** un adulto con cuenta gestiona a uno o varios jugadores menores, que no tienen cuenta propia

### Modelo de datos

| Entidad                          | Campos principales                                                            | Relaciones                                                                     |
|----------------------------------|-------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `User` (usuario)                 | email, passwordHash, active                                                   | 0..1 Person (el administrador inicial no tiene), N:M Role                      |
| `Role` (rol)                     | name (MEMBER, GUARDIAN, COACH, COORDINATOR, COMMUNICATIONS, TREASURER, ADMIN) | N:M User                                                                       |
| `Guardianship` (tutela)          | relationship                                                                  | guardian (Person con cuenta) a minor (Person)                                  |
| `Announcement` (aviso)           | title, body, publishedAt, audience (CLUB, TEAM o PERSON)                      | N:1 author (User), N:1 team (Team, opcional), N:1 recipient (Person, opcional) |
| `AccountToken` (token de cuenta) | type (INVITATION o PASSWORD_RESET), tokenHash, expiresAt, usedAt              | N:1 User                                                                       |

Destinatario de un aviso: `CLUB` no lleva equipo ni persona, `TEAM` solo lleva `team` y `PERSON` solo `recipient`; un `CHECK` (`ck_announcements_audience`) lo garantiza en la base de datos. Un aviso a un equipo lo ven sus jugadores, los tutores de sus menores y sus entrenadores. Un aviso a una persona lo ve esa persona o, si es menor, sus tutores. El autor es un `User` porque representa la cuenta que lo escribe.

`Person` gana `phone` y el resto de datos privados. `User` se enlaza con su `Person`; los menores siguen siendo `Person` sin `User`.

### Endpoints REST

| Método   | Ruta                                                                 | Acceso                                     |
|----------|----------------------------------------------------------------------|--------------------------------------------|
| POST     | `/api/v1/auth/invitation/accept`                                     | Público (con token)                        |
| POST     | `/api/v1/auth/password-reset`, `/api/v1/auth/password-reset/confirm` | Público                                    |
| GET, PUT | `/api/v1/me`                                                         | Autenticado                                |
| GET      | `/api/v1/me/teams`, `/api/v1/me/matches`, `/api/v1/me/announcements` | Autenticado                                |
| GET      | `/api/v1/me/dependants`                                              | Tutor                                      |
| GET      | `/api/v1/teams/{id}/roster`                                          | Entrenador del equipo, coordinador o admin |
| POST     | `/api/v1/announcements`                                              | Según audiencia (ver matriz)               |
| CRUD     | `/api/v1/admin/users`, `/api/v1/admin/users/{id}/roles`              | Admin                                      |

### Tareas

- [ ] Desplegar `staging` y activar el despliegue automático de `develop`
- [ ] Email de aviso tras varios intentos de login fallidos
- [ ] Autorización por rol con `@PreAuthorize`, siguiendo la matriz de permisos
- [ ] Comprobar que un entrenador solo toca sus propios equipos y un tutor solo a sus menores (no basta con el rol)
- [ ] Flujo de invitación y recuperación de contraseña con `AccountToken`
- [ ] Envío de emails con la interfaz EmailSender (SMTP a Mailpit en local y API HTTP de Brevo en staging y prod, porque Render gratuito bloquea SMTP): plantillas Thymeleaf, envío asíncrono con `@Async`, clave en `MAIL_API_KEY`, Mailpit en local y sandbox de Brevo en `staging`
- [ ] Frontend: sesión, rutas protegidas y menú según rol
- [ ] Frontend: área de socio y área de entrenador
- [ ] Tests de seguridad: cada endpoint devuelve 401/403 a quien no debe
- [ ] Primeros tests E2E con Playwright (login)

**Criterio de salida:** un socio, un tutor, un entrenador y un admin de prueba entran con su cuenta y cada uno ve solo su área.

## Fase 3: Funciones de equipo

**Objetivo:** que los entrenadores gestionen el día a día desde la web y que la clasificación se calcule sola a partir de los resultados. Es la fase con más lógica de negocio y la que más luce en una entrevista.

### Qué incluye

- **Convocatorias:** el entrenador convoca jugadores para un partido y cada uno (o su tutor) responde «asisto» o «no asisto»
- **Entrenamientos:** horario semanal por equipo y pase de lista con estadísticas de asistencia
- **Resultados set a set:** el entrenador, el coordinador o el admin introduce los puntos de cada set
- **Clasificación automática:** se recalcula al guardar o corregir un resultado
- **Historial de cambios:** queda registrado quién cambió un resultado, con el valor anterior y el nuevo
- **Estadísticas básicas:** racha del equipo, porcentaje de asistencia por jugador

### Cálculo de la clasificación

Sistema habitual en ligas de voleibol:

| Resultado       | Puntos ganador                    | Puntos perdedor                         |
|-----------------|-----------------------------------|-----------------------------------------|
| 3-0 o 3-1       | 3                                 | 0                                       |
| 3-2             | 2                                 | 1                                       |
| Incomparecencia | 3 (cuenta como 3-0, 25-0 por set) | 0, y se le restan los puntos de sanción |

Orden de desempate: puntos, partidos ganados, cociente de sets (ganados / perdidos), cociente de puntos.

Reglas:

- Solo cuentan los partidos `FINISHED` y `FORFEITED`. Los `POSTPONED` y `CANCELLED` no suman nada.
- La sanción por incomparecencia se configura en cada competición (`forfeitPenaltyPoints`, 1 por defecto) y se muestra en la tabla como puntos de sanción.
- En los partidos del club se introducen los puntos de cada set. En los partidos entre rivales basta el marcador de sets (3-1); los puntos por set son opcionales.
- El cociente de puntos se calcula solo con los partidos que tienen puntos por set.

La clasificación deja de ser una tabla que se edita: pasa a calcularse en un `StandingService` a partir de los partidos. Se puede guardar en caché y regenerar cuando cambia un resultado.

### Modelo de datos

| Entidad                           | Campos principales                                          | Relaciones                      |
|-----------------------------------|-------------------------------------------------------------|---------------------------------|
| `MatchSet` (set)                  | number, homePoints, awayPoints                              | N:1 Match                       |
| `CallUp` (convocatoria)           | response (PENDING, ATTENDING, NOT_ATTENDING), comment       | N:1 Match, N:1 Person           |
| `TrainingSession` (entrenamiento) | startsAt, durationMinutes                                   | N:1 Team, N:1 Venue             |
| `Attendance` (asistencia)         | present, absenceReason                                      | N:1 TrainingSession, N:1 Person |
| `AuditLog` (historial)            | entityType, entityId, action, oldValue, newValue, changedAt | N:1 changedBy (User)            |

### Endpoints REST

| Método | Ruta                                            | Acceso                                     |
|--------|-------------------------------------------------|--------------------------------------------|
| POST   | `/api/v1/matches/{id}/call-ups`                 | Entrenador del equipo, coordinador o admin |
| PUT    | `/api/v1/call-ups/{id}`                         | El jugador convocado o su tutor            |
| POST   | `/api/v1/matches/{id}/result`                   | Entrenador del equipo, coordinador o admin |
| CRUD   | `/api/v1/teams/{id}/trainings`                  | Entrenador del equipo, coordinador o admin |
| PUT    | `/api/v1/trainings/{id}/attendance`             | Entrenador del equipo, coordinador o admin |
| GET    | `/api/v1/teams/{id}/stats`                      | Entrenador del equipo, coordinador o admin |
| GET    | `/api/v1/admin/audit-log?entityType=&entityId=` | Admin                                      |

### Tareas

- [ ] Validar resultados: el ganador llega a 3 sets, sets a 25 (el quinto a 15) con diferencia de 2
- [ ] Registrar incomparecencias y aplicar la sanción de la competición
- [ ] Resultados de partidos entre rivales solo con marcador de sets
- [ ] `StandingService` con tests unitarios para cada caso (3-0, 3-2, empates, incomparecencia, partidos sin puntos por set)
- [ ] Recalcular la clasificación al corregir un resultado
- [ ] `AuditLog` en resultados
- [ ] Convocatorias y respuestas, también por el tutor de un menor
- [ ] Pase de lista y estadísticas de asistencia
- [ ] Frontend: formulario de resultado set a set
- [ ] Frontend: convocatoria en el área del entrenador y respuesta en el área del socio
- [ ] E2E con Playwright: introducir un resultado y ver la clasificación; responder una convocatoria

**Criterio de salida:** al introducir un resultado, la clasificación pública cambia sola y los tests del cálculo pasan.

## Fase 4: Extras e integraciones

**Objetivo:** conectar la web con servicios externos para que sea útil en el día a día del club. Cada extra es independiente: se pueden hacer en cualquier orden o dejar alguno fuera.

### Integraciones

| Extra              | Qué aporta                                            | Tecnología                                             | Prioridad |
|--------------------|-------------------------------------------------------|--------------------------------------------------------|-----------|
| Emails             | Avisos de convocatoria y recordatorios de cuota       | Brevo, con la base de envío de la fase 2               | Alta      |
| Calendario iCal    | Suscribirse al calendario de un equipo desde el móvil | Generar `.ics` (librería ical4j)                       | Alta      |
| Mapa de pabellones | Ver dónde se juega cada partido                       | Leaflet + OpenStreetMap, geocodificación con Nominatim | Media     |
| Cuotas             | Ver qué socios han pagado y avisar a los pendientes   | Entidad `MembershipFee` + tarea `@Scheduled`           | Media     |
| Pago online        | Pagar la cuota desde la web                           | Stripe en modo test                                    | Baja      |
| Exportar datos     | Listados de socios y asistencia en CSV o Excel        | Apache POI u OpenCSV                                   | Baja      |

### Modelo de datos

| Entidad                       | Campos principales                                                           | Relaciones             |
|-------------------------------|------------------------------------------------------------------------------|------------------------|
| `MembershipFee` (cuota)       | concept, amount, dueDate, status (PENDING, PAID, OVERDUE, CANCELLED), paidAt | N:1 Person, N:1 Season |
| `Payment` (pago)              | amount, method, externalReference (id de Stripe), paidAt                     | N:1 MembershipFee      |
| `Notification` (notificación) | type, sentAt, status                                                         | N:1 User               |

Estados de la cuota: nace `PENDING`; pasa a `PAID` al registrarse el pago; una tarea programada la pasa a `OVERDUE` cuando vence sin pagar (y aún puede pagarse); `CANCELLED` cubre bajas y errores sin borrar el registro. La cuota es de la persona (el jugador); si es menor, la ve y la paga su tutor.

### Endpoints REST

| Método | Ruta                              | Acceso                                       |
|--------|-----------------------------------|----------------------------------------------|
| GET    | `/api/v1/teams/{id}/calendar.ics` | Público                                      |
| GET    | `/api/v1/me/fees`                 | Autenticado (las suyas y las de sus menores) |
| CRUD   | `/api/v1/fees`                    | Tesorero o admin                             |
| POST   | `/api/v1/fees/{id}/payments`      | El titular, su tutor, tesorero o admin       |
| POST   | `/api/v1/webhooks/stripe`         | Stripe (firma verificada)                    |
| GET    | `/api/v1/exports/members`         | Tesorero o admin                             |

### Tareas

- [ ] Emails de convocatoria y de recordatorio de cuota, reutilizando las plantillas de la fase 2 y el envío asíncrono con `@Async`
- [ ] Endpoint iCal y botón «Añadir a mi calendario»
- [ ] Geocodificar la dirección de cada instalación al guardarla y mostrar el mapa
- [ ] Gestión de cuotas con sus cuatro estados, tarea diaria de vencimiento y recordatorio semanal
- [ ] `AuditLog` en cuotas y pagos
- [ ] Integración con Stripe y webhook de confirmación de pago
- [ ] Claves de APIs en variables de entorno, nunca en el repositorio

**Criterio de salida:** un socio recibe por email su convocatoria y su recordatorio de cuota, y puede suscribirse al calendario de su equipo.

## Fase 5: Pulido y despliegue final

**Objetivo:** dejar el proyecto listo para enseñar en entrevistas y, si llega el caso, presentarlo a un club.

### Calidad

- [ ] Cobertura de tests razonable en services (meta orientativa: 70 %)
- [ ] Tests de integración de los endpoints principales (Testcontainers con MySQL)
- [ ] E2E con Playwright de los 3 o 4 flujos críticos, ejecutados en la CI antes de cada release
- [ ] Revisar consultas N+1 en JPA (`@EntityGraph` o `JOIN FETCH`)
- [ ] Logs con niveles correctos y sin datos personales
- [ ] Captura de errores con Sentry en backend y frontend

### Experiencia de usuario

- [ ] Revisión completa en móvil (la mayoría de familias entrará desde ahí)
- [ ] Accesibilidad básica: contraste, textos alternativos, navegación con teclado
- [ ] Estados de carga, vacío y error en todas las pantallas
- [ ] SEO básico: títulos, descripciones y vista previa al compartir noticias en redes (etiquetas Open Graph servidas por el SSR)

### Despliegue

- [ ] HTTPS en el subdominio gratuito de Render; dominio propio solo si más adelante hay presupuesto

La CI básica y la protección de ramas ya existen desde la fase 0, y el despliegue a producción con aprobación y las copias de seguridad, desde la fase 1.

### Portfolio

- [ ] README con descripción, capturas, stack, diagrama ER y cómo arrancarlo con Docker
- [ ] Usuarios de demo (socio, tutor, entrenador, admin) para quien revise el proyecto
- [ ] Enlace a Swagger público
- [ ] Manual corto de uso del panel de administración
- [ ] Preparar una explicación de 2 minutos: problema, solución y decisiones técnicas

**Criterio de salida:** web en producción con HTTPS, CI en verde y README que permite a cualquiera entender y arrancar el proyecto.

## Transversal a todas las fases

### Protección de datos (RGPD y menores)

Un club de voleibol tiene jugadores menores. Aunque los datos sean de ejemplo, el diseño lo contempla desde la fase 1: es una buena práctica y facilita adaptarlo a un club real.

- [ ] Los menores no tienen cuenta: son una `Person` gestionada por su tutor
- [ ] Campo de consentimiento de imagen por persona, dado por el tutor si es menor
- [ ] En la parte pública, cada persona aparece siempre con nombre e inicial del apellido («Lucía M.»), con o sin consentimiento; la foto solo se muestra con consentimiento de imagen y, sin él, se usa un avatar genérico
- [ ] Datos personales y cuotas solo en el área privada
- [ ] Páginas de política de privacidad, aviso legal y cookies
- [ ] Poder exportar los datos de una persona y borrarlos si lo pide; el borrado anonimiza a la persona y conserva resultados y pagos

### Auditoría y borrados

- Las tablas de negocio llevan `created_at`, `updated_at`, `created_by` y `updated_by` desde la fase 1 (auditoría de Spring Data). La tabla de roles de usuario solo guarda quién y cuándo se creó cada fila, y el historial de cambios y las tablas de tokens tienen sus propias fechas.
- Borrado lógico (`deleted_at`) solo en personas, usuarios, noticias y cuotas. El resto se borra de verdad y la base de datos lo impide si hay dependencias.
- Historial de cambios (`AuditLog`, con valor anterior y nuevo) en resultados desde la fase 3 y en cuotas y pagos desde la fase 4.

### Monitorización

- `/actuator/health` y un monitor externo gratuito que avisa por email si la web cae, desde el primer despliegue (fase 1).
- Captura de errores con Sentry en backend y frontend, en la fase 5.

### Convenciones de trabajo

- **Flujo de trabajo** (ramas, commits, PRs, documentación): en el [documento de pautas](pautas-proyecto.md).
- **Reglas de código, entornos y bases de datos:** en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md).
- **Permisos por rol:** en la [matriz de permisos](../02-requisitos/matriz-permisos.md).
- **Colores:** en la [paleta Atlántico y arena](../02-requisitos/paleta-colores.md).
- **Idioma:** clases, tablas y rutas de la API en inglés; las tablas de este plan indican entre paréntesis el término del dominio en español.

### Riesgos

| Riesgo                                                                                                                            | Plan B                                                                                                                                   |
|-----------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| El proyecto crece demasiado                                                                                                       | Cerrar fase 1 y desplegar antes de empezar la siguiente                                                                                  |
| Sin fechas, el proyecto se alarga sin notarlo                                                                                     | Objetivo escrito en cada sprint y retrospectiva al cerrarlo; si dos sprints seguidos no cumplen su objetivo, recortar alcance de la fase |
| La planificación se come el tiempo                                                                                                | Los 12 pasos se detallan solo para la fase siguiente                                                                                     |
| Poco tiempo por las prácticas                                                                                                     | Sesiones cortas con tareas pequeñas del Kanban                                                                                           |
| Coste del hosting                                                                                                                 | Render y TiDB Cloud Starter en planes gratuitos, sin tarjeta; `staging` no se despliega hasta cerrar la fase 1                           |
| Versiones muy recientes con pocos tutoriales                                                                                      | Documentación oficial primero; anotar los problemas como notas de aprendizaje                                                            |
| Presentarlo más adelante a un club real                                                                                           | Revisar requisitos con ellos, sustituir datos de ejemplo y ajustar el reglamento de su federación                                        |
| La demo tarda en responder tras estar parada (arranque en frío de cerca de 1 minuto, hasta 2 si están dormidos los dos servicios) | Aviso en el README; abrirla un par de minutos antes de enseñarla en una entrevista                                                       |
| Con staging desplegado hay 4 servicios repartiendo las 750 horas gratuitas al mes de Render                                       | Revisar el consumo en el panel de Render a mitad de mes; el monitor solo vigila prod                                                     |
| Sin dominio propio, los emails salen desde una dirección verificada (por ejemplo, un Gmail) y pueden llegar a spam                | Avisarlo en el README y probar la demo con cuentas propias; con dominio propio, configurar SPF, DKIM y DMARC                             |
| Un plan gratuito cambia de condiciones o desaparece                                                                               | Configuración solo por variables de entorno y Docker, para poder cambiar de proveedor sin tocar el código                                |

### Registro de decisiones

Anoto aquí cada decisión importante, la más reciente arriba.

| Fecha      | Decisión                                                                                                                         | Motivo                                                                                                                                                  |
|------------|----------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| 2026-11-22 | Smoke tras cada despliegue y rendimiento con k6 contra `staging` antes de cada release                                           | Comprobar que cada despliegue está vivo y detectar regresiones de rendimiento sin cargar `prod` ni gastar la cuota gratuita                             |
| 2026-11-23 | Contrato OpenAPI de la fase 1: `GET /news/by-slug/{slug}`, `GET /news/{id}`, `GET /competitions`, `GET /matches/{id}`, `/matches` paginado, 20 por página e imágenes de 5 MB | OpenAPI no admite `/news/{slug}` y `/news/{id}` a la vez, y editar por slug es frágil; detalle en `docs/05-api/README.md` |
| 2026-11-22 | Bloque A de la fase 0 terminado: CI con 4 comprobaciones obligatorias y rulesets en `main` y `develop` | Desde aquí nada entra en esas ramas sin PR y CI en verde; Spotless, Checkstyle y Husky siguen como issues aparte |
| 2026-11-16 | `develop` como rama por defecto en GitHub | `Closes #N` solo cierra issues al fusionar en la rama por defecto, y todos los PRs del día a día van a `develop` |
| 2026-11-12 | Migraciones desde GitHub Actions antes del deploy hook; emails por la API HTTP de Brevo; el SSR llama a la URL pública de la API | El plan gratuito de Render no tiene pre-deploy command, bloquea los puertos SMTP y no recibe tráfico de red privada                                     |
| 2026-11-12 | Límite de intentos de login por email + IP y por IP desde la fase 1, en memoria y siempre con 401 genérico                       | El bloqueo por cuenta con 423 revelaba qué emails existen y permitía a cualquiera dejar bloqueado al único administrador                                |
| 2026-11-12 | Envío de emails con Brevo desde la fase 2                                                                                        | La invitación y la recuperación de contraseña de la fase 2 necesitan email; SendGrid ya no tiene plan gratuito y Brevo permite 300 emails al día gratis |
| 2026-11-12 | Refresh token y sesión deslizante desde la fase 1 (2 h sin actividad, 7 días como máximo)                                        | Que el admin no pierda la sesión mientras trabaja y no escribir un login provisional que habría que tirar en la fase 2                                  |
| 2026-11-12 | Proxy de la API en el servidor SSR: el navegador solo habla con el frontend; cookie de refresco sin Domain y CORS desactivado    | onrender.com está en la Public Suffix List: API y frontend serían sitios distintos y SameSite=Strict no funcionaría                                     |
| 2026-11-10 | Hosting en Render (API y frontend) y TiDB Cloud Starter (base de datos), con el subdominio gratuito de Render                    | Presupuesto 0 € y sin tarjeta; se acepta el arranque en frío. TiDB es la única base compatible con MySQL gratuita y sin tarjeta encontrada              |
| 2026-11-10 | Nombre provisional del club: Los Chicharros V.C.                                                                                 | Poder avanzar con el club ficticio; el definitivo se confirma antes del primer despliegue                                                               |
| 2026-11-10 | Paleta de colores Atlántico y arena                                                                                              | Elegida entre varias propuestas; detalle y reglas de contraste en [paleta-colores.md](../02-requisitos/paleta-colores.md)                                                            |
| 2026-11-10 | `Person` separada de `User`; la cuenta es opcional                                                                               | Los menores no tienen email ni deben tener login; los gestiona su tutor                                                                                 |
| 2026-11-10 | Login JWT mínimo con un único admin ya en la fase 1                                                                              | Proteger el panel sin código provisional que haya que tirar                                                                                             |
| 2026-11-10 | Angular con SSR desde el inicio                                                                                                  | La vista previa en redes y el SEO no funcionan con una SPA pura                                                                                         |
| 2026-11-10 | Imágenes con interfaz `ImageStorage` y Cloudinary                                                                                | El disco de los hostings se borra en cada despliegue                                                                                                    |
| 2026-11-10 | Refresh token en cookie httpOnly con rotación; `AccountToken` con tipo; bloqueo tras 5 fallos                                    | Seguridad frente a XSS y poder cerrar sesión de verdad                                                                                                  |
| 2026-11-10 | Estado `FORFEITED` con sanción configurable; rivales solo con marcador de sets                                                   | Clasificación realista sin cargar al admin de datos                                                                                                     |
| 2026-11-10 | Spring Boot 4.1, Java 25, Angular 22, Node 24 LTS, pnpm                                                                          | Spring Boot 3.x ya no recibe parches gratuitos; empezar con versiones con soporte                                                                       |
| 2026-11-10 | Fase 0 en dos bloques; 12 pasos solo para la fase 1                                                                              | Evitar meses de planificación sin código                                                                                                                |
| 2026-11-10 | `staging` se despliega al cerrar la fase 1                                                                                       | Ahorrar hosting y configuración antes del MVP                                                                                                           |
| 2026-11-10 | CI básica en la fase 0; Testcontainers obligatorio                                                                               | Las pautas exigen CI en verde desde el primer PR                                                                                                        |
| 2026-11-10 | Cuotas con cuatro estados                                                                                                        | Distinguir vencidas y anuladas sin borrar registros                                                                                                     |
| 2026-11-10 | Rutas por recurso con permiso por rol; `/admin` solo configuración                                                               | Evitar rutas duplicadas por rol                                                                                                                         |
| 2026-11-10 | Matriz rol × acción como documento propio                                                                                        | Dejar escritos los permisos de los 8 roles                                                                                                              |
| 2026-11-10 | Sin fechas: orden de fases y sprints de dos semanas                                                                              | Proyecto personal compatible con las prácticas                                                                                                          |
| 2026-11-10 | Actuator y monitor de caídas en la fase 1; Sentry en la fase 5                                                                   | Saber si la web está caída o falla                                                                                                                      |
| 2026-11-10 | `created_by`/`updated_by`, borrado lógico selectivo e historial de cambios                                                       | Trazabilidad de resultados y cuotas                                                                                                                     |
| 2026-11-10 | Vitest y Playwright para el frontend                                                                                             | Concretar las herramientas y cubrir los flujos críticos                                                                                                 |
| 2026-11-10 | Noticias en Markdown saneado en el backend                                                                                       | Formato sencillo y seguro frente a XSS                                                                                                                  |
| 2026-11-03 | Maven como herramienta de build                                                                                                  | Es lo más extendido en empresas y proyectos Spring, y tiene menos curva de aprendizaje                                                                  |
| 2026-11-03 | Nombres en inglés en código, tablas y API                                                                                        | Cumplir la regla de código en inglés; las equivalencias están en las reglas técnicas                                                                    |
| 2026-11-03 | Frontend con Angular                                                                                                             | Elegido en las pautas; se registra como ADR-001                                                                                                         |
| 2026-11-03 | Club ficticio, sin contactar con ningún club                                                                                     | Es un proyecto de portfolio; si lo presento a un club, lo adapto                                                                                        |
| 2026-11-03 | Backend con Spring Boot + MySQL                                                                                                  | Son las tecnologías que ya conozco                                                                                                                      |

### Preguntas abiertas

- [ ] ¿Nombre definitivo y escudo del club? Provisional: Los Chicharros V.C. (de ahí sale también el prefijo de los selectores Angular)
