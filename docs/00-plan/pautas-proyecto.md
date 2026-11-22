# Pautas del proyecto: Web del club de voleibol

Última revisión: 2026-11-22.

Estas son las reglas obligatorias para llevar el proyecto como se haría en una empresa. Cualquier persona que se incorpore debe poder leerlas y seguir trabajando sin preguntar. El qué se construye está en el [plan del proyecto](plan-proyecto.md), quién puede hacer qué en la [matriz de permisos](../02-requisitos/matriz-permisos.md) y cómo se escribe el código en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md); este documento define cómo se trabaja.

## Decisiones tomadas

| Fecha | Decisión | Elección |
| --- | --- | --- |
| 2026-11-03 | Idioma | Código y commits en inglés, documentación en español |
| 2026-11-03 | Licencia | MIT |
| 2026-11-03 | Frontend | Angular (ADR-001) |
| 2026-11-03 | Dedicación | 5-10 h/semana |
| 2026-11-10 | Renderizado | Angular con SSR (ADR-002) |
| 2026-11-10 | Calendario | Sin fechas: orden de fases y sprints de dos semanas |
| 2026-11-10 | Planificación | Los 12 pasos se detallan solo para la fase siguiente |
| 2026-11-10 | CI | Desde la fase 0, junto a la protección de ramas |
| 2026-11-10 | Tests | Testcontainers obligatorio; Vitest y Playwright en el frontend |
| 2026-11-10 | Gestor de paquetes | pnpm; nunca npm |
| 2026-11-22 | Tests | Smoke tras cada despliegue y rendimiento con k6 antes de cada release; cómo se hacen, en la guía de tests |

## Principios

Trabajo como si fuera parte de un equipo, aunque esté solo: cada cambio tiene una tarea, una rama, una revisión y queda documentado.

1. **Nada se programa sin tarea.** Todo trabajo empieza con un issue en GitHub con descripción y criterios de aceptación.
2. **Nada entra en `main` sin pull request.** Ni siquiera un cambio de una línea.
3. **Primero se planifica, luego se programa.** Ninguna fase empieza sin su diseño (modelo de datos, endpoints, diagramas) revisado.
4. **Si no está documentado, no está hecho.** El código, la API y las decisiones se documentan a la vez que se hacen, no al final.
5. **Iteraciones cortas.** Trabajo en sprints de 2 semanas con un objetivo claro y una demo al final.
6. **El proyecto debe poder continuarlo otra persona.** Cualquiera debe poder clonar el repositorio, leer el README y tenerlo funcionando en menos de 15 minutos.
7. **Las decisiones se justifican.** Cada decisión técnica importante queda registrada con su motivo y las alternativas descartadas.
8. **Todo en un solo idioma por contexto.** Código y commits en inglés; documentación funcional en español.

## Documentación

Toda la documentación técnica vive en el repositorio, en Markdown, junto al código. Así se versiona con él y nunca se queda desactualizada en otro sitio.

### Estructura de la carpeta `docs/`

```
docs/
├── 00-plan/              Plan del proyecto y estas pautas
├── 01-vision/            Visión, alcance, glosario
├── 02-requisitos/        Historias de usuario, matriz de permisos, requisitos no funcionales
├── 03-arquitectura/      Arquitectura, stack, diagramas C4
├── 04-datos/             Modelo ER, diccionario de datos
├── 05-api/               Convenciones REST, códigos de error
├── 06-diagramas/         Flujos, secuencias, estados (Mermaid)
├── 07-desarrollo/        Guía de instalación y de contribución, reglas técnicas
├── 08-despliegue/        Entornos, variables, CI/CD, backups
└── adr/                  Registro de decisiones (ADR-001, ADR-002…)
```

### Documentos obligatorios

| Documento | Ubicación | Contenido | Se actualiza |
| --- | --- | --- | --- |
| README | `README.md` | Qué es, capturas, stack, cómo arrancarlo, enlaces a `docs/` | Cada release |
| Guía de contribución | `CONTRIBUTING.md` | Ramas, commits, PRs, estilo de código | Cuando cambie una regla |
| Changelog | `CHANGELOG.md` | Cambios por versión (formato Keep a Changelog) | Cada release |
| Licencia | `LICENSE` | MIT | Una vez |
| Glosario | `docs/01-vision/glosario.md` | Términos del dominio: jornada, convocatoria, set, categoría… | Al aparecer un término nuevo |
| Matriz de permisos | `docs/02-requisitos/matriz-permisos.md` | Qué puede hacer cada rol | Al añadir una acción o un rol |
| Diccionario de datos | `docs/04-datos/` | Cada tabla y columna: tipo, obligatoriedad, significado | Con cada migración |
| ADR | `docs/adr/` | Una decisión por archivo: contexto, decisión, alternativas, consecuencias | Al tomar una decisión |
| API | Swagger (`/swagger-ui`) | Endpoints generados desde el código con ejemplos | Automático |
| Javadoc | En el código | Javadoc en los servicios Java y TSDoc en los servicios de Angular, en español (ver «Comentarios» en las reglas técnicas) | Con el código |

### Reglas

- Un PR que cambia el comportamiento sin actualizar la documentación afectada no se fusiona.
- Un PR que cambia quién puede hacer algo actualiza la matriz de permisos y su test de seguridad.
- Las decisiones se escriben como ADR cortos (menos de una página) con la plantilla de Michael Nygard.
- Los diagramas se guardan como código (Mermaid) para que se puedan revisar en un PR.
- Este documento de pautas, el plan, las reglas técnicas, la matriz de permisos y la paleta de colores se copian a `docs/` cuando se cree el repositorio.
- **Fuente de verdad:** hasta crear el repositorio, mandan los documentos vivos (plan, pautas, reglas técnicas) y la matriz y la paleta del proyecto; las copias del proyecto son una foto que se actualiza al cerrar cada revisión. Desde que existe el repositorio, solo manda `docs/`, y cualquier cambio va por PR.

## GitHub y flujo de trabajo

Un único repositorio (monorepo) con `backend/`, `frontend/` y `docs/`. GitHub concentra código, tareas, revisiones, CI y releases.

### Ramas

| Rama | Uso | Reglas |
| --- | --- | --- |
| `main` | Código en producción | Protegida: solo por PR desde `develop` o `hotfix/*`, CI en verde |
| `develop` | Integración de lo terminado | Protegida: solo por PR, CI en verde |
| `feature/<issue>-<descripcion>` | Nueva funcionalidad (`feature/12-crud-noticias`) | Sale de `develop` y vuelve a `develop` |
| `fix/<issue>-<descripcion>` | Corrección de un error en desarrollo | Sale de `develop` y vuelve a `develop` |
| `hotfix/<descripcion>` | Error urgente en producción | Sale de `main`, vuelve a `main` y a `develop` |
| `docs/<descripcion>` | Solo documentación | Sale de `develop` y vuelve a `develop` |

La CI (compilación, tests, formato y linter de backend y frontend) y la protección de ramas se configuran en la fase 0, antes del primer PR de código. Hasta cerrar la fase 1, `develop` solo pasa la CI; después se despliega sola en el entorno de pruebas (`staging`).

### Ciclo de una tarea

1. Crear el issue con la plantilla (historia de usuario, criterios de aceptación, etiquetas, milestone).
2. Moverlo a «En curso» en el tablero de GitHub Projects.
3. Crear la rama desde `develop` con el número del issue.
4. Hacer commits pequeños siguiendo Conventional Commits.
5. Abrir el pull request con la plantilla y `Closes #<issue>`.
6. Esperar a que la CI pase (compilación, tests, linter).
7. Autorrevisión: leer el diff completo en GitHub como si fuera de otra persona y marcar la checklist.
8. Fusionar con *squash merge* y borrar la rama.
9. El issue se cierra solo y pasa a «Hecho».

### Commits

Formato [Conventional Commits](https://www.conventionalcommits.org/es/v1.0.0/): `tipo(ámbito): descripción en imperativo`.

| Tipo | Cuándo | Ejemplo |
| --- | --- | --- |
| `feat` | Nueva funcionalidad | `feat(news): add pagination to news list` |
| `fix` | Corrección de error | `fix(auth): refresh token expiry check` |
| `docs` | Documentación | `docs(adr): add ADR-003 person and user split` |
| `test` | Tests | `test(standings): cover 3-2 match points` |
| `refactor` | Cambio sin alterar comportamiento | `refactor(matches): extract result validator` |
| `chore` | Configuración, dependencias | `chore: bump spring boot to 4.2` |
| `ci` | Pipelines | `ci: add backend test workflow` |

### Issues, etiquetas y milestones

- **Plantillas de issue:** historia de usuario, bug y tarea técnica (`.github/ISSUE_TEMPLATE/`).
- **Plantilla de PR:** qué cambia, cómo probarlo, capturas si hay interfaz, checklist de la Definition of Done.
- **Etiquetas:** tipo (`feature`, `bug`, `docs`, `tech-debt`), área (`backend`, `frontend`, `database`) y prioridad (`p1`, `p2`, `p3`).
- **Deuda técnica:** no se dejan comentarios `TODO` en el código; cada pendiente es un issue con la etiqueta `tech-debt`.
- **Milestones:** uno por fase del plan (Fase 1: MVP público…).
- **Tablero:** GitHub Projects con columnas Backlog, Listo, En curso, En revisión, Hecho.

### Versiones y releases

- Versionado semántico: `MAJOR.MINOR.PATCH`. Cada fase terminada es una versión menor (`v0.1.0` = fase 1).
- Al fusionar `develop` en `main` se crea una etiqueta y una release en GitHub con las notas del changelog.
- Antes de cada release se pasan los tests E2E, la prueba de rendimiento en `staging` y la checklist de release; después de desplegar, el smoke.
- `v1.0.0` cuando el proyecto esté completo y desplegado.

### Sprints sin fechas

- El proyecto no tiene calendario: avanza por orden de fases, en sprints de dos semanas.
- Cada sprint empieza con un objetivo escrito y los issues que caben en 5-10 h semanales.
- Cada sprint termina con una demo (captura o vídeo corto) y una nota de qué mejorar.
- El alcance se controla con el criterio de salida de cada fase. Si dos sprints seguidos no cumplen su objetivo, se recorta alcance de la fase en lugar de alargarla.

## Fase de planificación

La fase 0 del plan tiene dos bloques que avanzan en paralelo: **(A)** crear el repositorio con la CI, la protección de ramas y el entorno local, y **(B)** estos 12 pasos.

No se escribe código de negocio de una fase hasta completar sus 12 pasos en orden. Los pasos se detallan **solo para la fase siguiente**: ahora, para la fase 1; las fases 2 a 5 se quedan a alto nivel y se detallan al llegar. Cada paso produce un entregable en `docs/` y se cierra con su propio PR, para que quede revisado y versionado.

| # | Paso | Entregable | Ubicación |
| --- | --- | --- | --- |
| 1 | Visión y alcance: problema, objetivos, qué entra y qué no | Documento de visión | `docs/01-vision/` |
| 2 | Club ficticio y glosario del dominio | Ficha del club, glosario | `docs/01-vision/` |
| 3 | Roles y permisos | Matriz rol × acción (primera versión hecha: [matriz-permisos.md](../02-requisitos/matriz-permisos.md)) | `docs/02-requisitos/` |
| 4 | Casos de uso por rol | Diagrama de casos de uso | `docs/06-diagramas/` |
| 5 | Historias de usuario con criterios de aceptación y prioridad MoSCoW | Backlog en GitHub Issues | GitHub + `docs/02-requisitos/` |
| 6 | Requisitos no funcionales: rendimiento, seguridad, accesibilidad, RGPD, navegadores | Lista de requisitos medibles | `docs/02-requisitos/` |
| 7 | Modelo de datos: entidades, relaciones, restricciones | Diagrama ER y diccionario de datos | `docs/04-datos/` |
| 8 | Arquitectura: capas, componentes, integraciones externas | Diagramas C4 (contexto y contenedores) | `docs/03-arquitectura/` |
| 9 | Diseño de la API: recursos, rutas, DTOs, errores | Contrato OpenAPI inicial | `docs/05-api/` |
| 10 | Flujos principales de la fase | Diagramas de secuencia y de estados | `docs/06-diagramas/` |
| 11 | Wireframes de las pantallas clave y guía visual | Bocetos (Figma o Excalidraw); paleta ya elegida ([paleta-colores.md](../02-requisitos/paleta-colores.md)) | `docs/02-requisitos/wireframes.md` (imágenes en `docs/assets/wireframes/`) |
| 12 | Plan de sprints: issues agrupados en milestones, estimados | Tablero de GitHub Projects listo | GitHub |

Los pasos 1, 2, 3, 4 y 8 se hacen una vez para todo el proyecto y se revisan al empezar cada fase. El diagrama ER del paso 7 cubre todo el proyecto a alto nivel; el diccionario de datos, solo la fase que se va a programar.

### Reglas de la planificación

- Las historias siguen el formato: *Como [rol] quiero [acción] para [beneficio]*.
- Cada criterio de aceptación se escribe en formato *Dado / Cuando / Entonces* para que luego se convierta en un test.
- Las historias se estiman en puntos (1, 2, 3, 5, 8). Una historia de más de 8 se divide.
- Al terminar el paso 12 se hace una revisión completa: ¿cada historia tiene su pantalla, su endpoint, sus tablas y su fila en la matriz de permisos? Si falta algo, se corrige antes de programar.

## Diagramas

Los diagramas se escriben en **Mermaid** dentro de archivos `.md`: GitHub los muestra directamente y cada cambio se revisa en un PR como el código. Solo los wireframes se hacen en una herramienta visual (Figma o Excalidraw), exportando una imagen al repositorio.

| Diagrama | Qué muestra | Cuándo | Tipo Mermaid |
| --- | --- | --- | --- |
| Casos de uso | Qué puede hacer cada rol | Planificación, paso 4 | `flowchart` |
| Entidad-relación | Tablas, claves y cardinalidades | Paso 7 y con cada migración | `erDiagram` |
| C4 contexto | La web, sus usuarios y servicios externos (email, Cloudinary, Stripe, mapas) | Paso 8 | `C4Context` o `flowchart` |
| C4 contenedores | Frontend con SSR (Node), API, MySQL y cómo se comunican | Paso 8 | `flowchart` |
| Capas del backend | Controller, Service, Repository, DTOs, seguridad | Paso 8 | `flowchart` |
| Secuencia: login del admin | Login, access token y acceso a ruta protegida | Paso 10 de la fase 1 | `sequenceDiagram` |
| Secuencia: sesión completa | Refresh en cookie, rotación, cierre de sesión | Paso 10 de la fase 2 | `sequenceDiagram` |
| Secuencia: invitación y recuperación | Token de cuenta, email, nueva contraseña | Paso 10 de la fase 2 | `sequenceDiagram` |
| Secuencia: resultado y clasificación | Introducir sets, validar, recalcular tabla | Paso 10 de la fase 3 | `sequenceDiagram` |
| Flujo: convocatoria | Convocar, notificar, responder (incluido tutor) | Paso 10 de la fase 3 | `flowchart` |
| Estados: partido | Programado, en juego, finalizado, aplazado, cancelado, incomparecencia | Paso 10 de la fase 1 | `stateDiagram-v2` |
| Estados: cuota | Pendiente, pagada, vencida, anulada | Antes de la fase 4 | `stateDiagram-v2` |
| Secuencia: pago con Stripe | Crear pago, redirección, webhook, confirmar | Antes de la fase 4 | `sequenceDiagram` |
| Flujo de Git | Ramas, PRs y releases | Al crear el repositorio | `gitGraph` |
| Despliegue | Entornos, CI/CD, servidor y base de datos | Antes del primer despliegue | `flowchart` |
| Navegación | Mapa de pantallas por rol | Paso 11 | `flowchart` |

### Reglas de los diagramas

- Un diagrama por archivo, con título y fecha de última revisión.
- Si un PR cambia el modelo de datos o un flujo, actualiza su diagrama en el mismo PR.
- Los diagramas explican; no se dibuja todo por dibujar. Si un flujo es lineal y obvio, basta con una lista.

## Estándares de código y calidad

El detalle por lenguaje está en las reglas técnicas; aquí va lo esencial.

### Código

- **Backend:** un paquete por funcionalidad (`news/`, `matches/`, `users/`) con la entidad, el repository, el service, el controller y el mapper directamente dentro, y solo los DTOs en la subcarpeta `dto/`. Lo que no se usa fuera de la funcionalidad es package-private (sin `public`). El detalle está en las reglas técnicas.
- **Estilo Java:** Google Java Style, aplicado automáticamente con Spotless en la CI.
- **Frontend:** ESLint (angular-eslint) y Prettier con la configuración en el repositorio; siempre `pnpm`, nunca `npm`.
- **Nunca se exponen entidades JPA en la API:** siempre DTOs, con mapeo en una clase `mapper` (MapStruct).
- **Errores con formato común** (RFC 7807, `ProblemDetail` de Spring) y códigos HTTP correctos.
- **Base de datos:** cambios solo mediante migraciones Flyway; nunca se edita una migración ya fusionada.
- **Sin código muerto ni comentado:** el historial de Git ya lo guarda.
- **Pre-commit obligatorio:** Husky y lint-staged en la raíz del monorepo (con pnpm) formatean los ficheros cambiados antes de cada commit: Prettier y ESLint en el frontend, Spotless en el backend. La CI vuelve a comprobarlo.

### Tests

| Tipo | Qué cubre | Herramienta | Cuándo se ejecuta |
| --- | --- | --- | --- |
| Unitarios | Lógica de servicios (cálculo de clasificación, validaciones) | JUnit 5 + Mockito | Cada push y PR |
| Integración | Endpoints con base de datos real | Spring Boot Test + Testcontainers (MySQL), obligatorio | Cada push y PR |
| Seguridad | Cada fila de la matriz de permisos: 401/403 a quien no debe | MockMvc | Cada push y PR |
| Frontend | Componentes, servicios y formularios clave | Vitest | Cada push y PR |
| Extremo a extremo | 3 o 4 flujos críticos: login, publicar noticia, resultado y clasificación, convocatoria | Playwright | Antes de cada release |
| Smoke | Que el despliegue está vivo: salud de la API, portada con SSR, proxy de la API | `curl` en GitHub Actions | Después de cada despliegue |
| Rendimiento | Lecturas públicas con 20 usuarios virtuales y umbrales de p95 | k6, solo contra `staging` | Antes de cada release (desde que exista `staging`) |

Meta orientativa: 70 % de cobertura en servicios, medida con JaCoCo y visible en la CI.

Cómo se escribe, dónde vive y cómo se ejecuta cada tipo: [guía de tests](../07-desarrollo/guia-tests.md).

### Definition of Ready (una historia puede empezarse cuando…)

- [ ] Tiene descripción en formato de historia de usuario
- [ ] Tiene criterios de aceptación *Dado / Cuando / Entonces*
- [ ] Está estimada y tiene prioridad y milestone
- [ ] Su diseño (tablas, endpoint, pantalla, permisos) está definido
- [ ] No depende de otra historia sin terminar

### Definition of Done (una historia está terminada cuando…)

- [ ] Cumple todos sus criterios de aceptación
- [ ] Tiene tests y la CI está en verde
- [ ] El código pasa el linter y la autorrevisión del PR
- [ ] La documentación afectada está actualizada (Swagger, diagramas, diccionario de datos, matriz de permisos)
- [ ] Funciona en móvil y escritorio
- [ ] Está fusionada en `develop` y el issue cerrado

## Seguridad y configuración

- **Ningún secreto en el repositorio:** contraseñas, claves de API y el secreto JWT van en variables de entorno. Se sube un `.env.example` con las claves vacías.
- **`.gitignore` desde el primer commit:** `.env`, `target/`, `node_modules/`, archivos del IDE.
- **Escaneo automático:** activar Dependabot (dependencias) y secret scanning en GitHub.
- **Entornos separados:** `dev` (local con Docker), `test` (Testcontainers), `prod` (desplegado) y, al cerrar la fase 1, `staging`; cada uno con su base de datos y su perfil de Spring.
- **Datos de prueba ficticios:** nunca datos reales de personas, ni en desarrollo ni en la demo.
- **Validación en el backend siempre**, aunque el frontend ya valide.
- **Contenido de usuarios saneado en el backend:** las noticias se escriben en Markdown y el HTML resultante pasa por una lista blanca de etiquetas.
- **Sesión:** refresh token en cookie httpOnly y access token solo en memoria; nunca en `localStorage`.
- **Revisar el OWASP Top 10** antes de cada release: inyección, control de acceso, exposición de datos.
- **Si se sube un secreto por error,** se revoca y se cambia inmediatamente; borrarlo del historial no basta.

## Monitorización

- **Salud:** `/actuator/health` desde el primer despliegue, con un monitor externo gratuito que avisa por email si la web cae.
- **Errores:** Sentry en backend y frontend en la fase 5, sin datos personales en los eventos.
- **Trazabilidad:** quién creó y modificó cada registro (`created_by`, `updated_by`) y, en resultados y cuotas, historial con el valor anterior y el nuevo.

## Qué más añadir

Sugerencias por adoptar. La planificación de sprint, la demo y la retrospectiva ya son obligatorias (ver Sprints sin fechas).

### Ceremonias ágiles (en versión de una sola persona)

- [ ] **Diario de trabajo:** un comentario breve al final de cada sesión (qué hice, qué sigue, bloqueos)

### Calidad y automatización

- [ ] **SonarCloud** gratuito para proyectos públicos: análisis de calidad y badge en el README
- [ ] **Badges en el README:** build, cobertura, versión, licencia
- [ ] **Colección de Postman o Bruno** en el repositorio para probar la API

### Para quien continúe el proyecto

- [ ] **Guía de onboarding:** de cero a la app funcionando, paso a paso, probada en un equipo limpio
- [ ] **Datos semilla y usuarios de demo** documentados (credenciales de prueba por rol)
- [ ] **Runbook:** qué hacer si se cae la web, cómo restaurar un backup, cómo rotar secretos

### Para el portfolio

- [ ] **Postmortems o notas de aprendizaje:** problemas difíciles y cómo los resolví (dan mucho juego en entrevistas)
- [ ] **Métricas del proyecto:** issues cerrados, PRs y cobertura, para contarlo con datos
