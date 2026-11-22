# Historias de usuario de la fase 1

Última revisión: 2026-11-22.

Backlog de la fase 1 (MVP público), entregable del paso 5 de la planificación. Cada historia es también un issue en GitHub con la plantilla de historia de usuario, en el milestone «Fase 1: MVP público». Los permisos de cada acción están en la [matriz de permisos](matriz-permisos.md); en la fase 1 solo existen el visitante y el administrador ([casos de uso](../06-diagramas/casos-de-uso.md)).

Las tareas técnicas de la fase (Swagger, `/actuator/health`, despliegue, copias de seguridad, proxy de la API) no son historias: se crean como tareas técnicas en el paso 12, al planificar los sprints.

## Resumen

| Id | Historia | MoSCoW | Puntos |
| --- | --- | --- | --- |
| H01 | Portada con lo último del club | Must | 5 |
| H02 | Listado de noticias | Must | 3 |
| H03 | Detalle de noticia | Must | 3 |
| H04 | Equipos y su plantilla | Must | 5 |
| H05 | Calendario de partidos | Must | 5 |
| H06 | Clasificación y resultados | Must | 5 |
| H07 | Páginas del club | Should | 2 |
| H08 | Páginas legales | Must | 2 |
| H09 | Iniciar sesión | Must | 5 |
| H10 | Mantener y cerrar la sesión | Must | 8 |
| H11 | Bloquear intentos repetidos de login | Must | 3 |
| H12 | Gestionar noticias | Must | 5 |
| H13 | Editor Markdown de noticias | Should | 5 |
| H14 | Subir imágenes | Must | 5 |
| H15 | Temporadas, categorías e instalaciones | Must | 3 |
| H16 | Gestionar personas | Must | 3 |
| H17 | Gestionar equipos y plantillas | Must | 5 |
| H18 | Gestionar competiciones y jornadas | Must | 3 |
| H19 | Gestionar partidos | Must | 5 |
| H20 | Introducir el marcador de sets | Must | 2 |
| H21 | Editar la clasificación a mano | Must | 3 |

Total: 21 historias y 85 puntos.

## H01. Portada con lo último del club

Como **visitante** quiero **ver en la portada las últimas noticias, los próximos partidos y los últimos resultados** para **enterarme de lo importante sin buscar**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** Página `/`; `GET /api/v1/news`, `GET /api/v1/matches`; renderizada en servidor.

**Criterios de aceptación**

- **Dado** que hay noticias publicadas y partidos programados y jugados, **cuando** abro la portada, **entonces** veo las 3 últimas noticias (las destacadas primero), los 5 próximos partidos del club y los 5 últimos resultados.
- **Dado** que no hay ninguna noticia ni partido, **cuando** abro la portada, **entonces** veo un mensaje de que aún no hay contenido, no una página rota.
- **Dado** que comparto el enlace de la portada en una red social, **cuando** la red genera la vista previa, **entonces** aparecen el título y la descripción del club.

## H02. Listado de noticias

Como **visitante** quiero **ver todas las noticias paginadas y filtrarlas por tipo** para **encontrar crónicas, eventos o comunicados**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** Página `/noticias`; `GET /api/v1/news?type=&page=`; 10 por página.

**Criterios de aceptación**

- **Dado** que hay 25 noticias publicadas, **cuando** abro el listado, **entonces** veo las 10 más recientes y puedo pasar a las páginas 2 y 3.
- **Dado** que hay crónicas y comunicados, **cuando** filtro por «Crónicas», **entonces** solo veo crónicas.
- **Dado** que una noticia está en borrador (sin fecha de publicación), **cuando** abro el listado sin sesión, **entonces** esa noticia no aparece.

## H03. Detalle de noticia

Como **visitante** quiero **leer una noticia completa y compartirla con una buena vista previa** para **informarme y difundir lo que hace el club**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** Página `/noticias/{slug}`; `GET /api/v1/news/{slug}`; Markdown convertido y saneado en el backend; Open Graph.

**Criterios de aceptación**

- **Dado** que existe una noticia publicada, **cuando** abro su enlace, **entonces** veo título, fecha, imagen y el cuerpo con formato.
- **Dado** que comparto el enlace en una red social, **cuando** la red genera la vista previa, **entonces** aparecen el título, el resumen y la imagen de la noticia.
- **Dado** que el slug no existe o la noticia es un borrador, **cuando** abro el enlace, **entonces** veo una página 404.

## H04. Equipos y su plantilla

Como **familiar o aficionado** quiero **ver los equipos del club por categoría y la plantilla de cada uno** para **conocer a los jugadores y entrenadores**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** Páginas `/equipos` y `/equipos/{id}`; `GET /api/v1/teams`, `GET /api/v1/teams/{id}`.

**Criterios de aceptación**

- **Dado** que hay equipos en la temporada activa, **cuando** abro la página de equipos, **entonces** los veo agrupados por categoría, de alevín a sénior.
- **Dado** que abro la ficha de un equipo, **cuando** veo la plantilla, **entonces** cada jugador aparece con nombre, inicial del apellido y dorsal, y los entrenadores aparte.
- **Dado** que un jugador no tiene consentimiento de imagen, **cuando** veo la plantilla, **entonces** se muestra un avatar genérico en lugar de su foto.

## H05. Calendario de partidos

Como **familiar o aficionado** quiero **ver los próximos partidos y filtrarlos por equipo y pabellón** para **saber cuándo y dónde juega el equipo que sigo**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** Página `/calendario`; `GET /api/v1/matches?teamId=&from=&to=`.

**Criterios de aceptación**

- **Dado** que hay partidos programados, **cuando** abro el calendario, **entonces** veo los próximos partidos ordenados por fecha con hora local, rival y pabellón.
- **Dado** que filtro por un equipo, **cuando** aplico el filtro, **entonces** solo veo los partidos de ese equipo.
- **Dado** que un partido está aplazado o cancelado, **cuando** veo el calendario, **entonces** aparece marcado con su estado.

## H06. Clasificación y resultados

Como **familiar o aficionado** quiero **ver la clasificación de cada competición y los resultados por jornada** para **seguir cómo va el equipo en su liga**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** Página `/clasificacion/{competicion}`; `GET /api/v1/competitions/{id}/standings`, `GET /api/v1/competitions/{id}/matchdays`.

**Criterios de aceptación**

- **Dado** que una competición tiene partidos finalizados, **cuando** abro su clasificación, **entonces** veo la tabla con puntos, partidos jugados, ganados, perdidos, sets y puntos de sanción, ordenada.
- **Dado** que elijo una jornada, **cuando** la abro, **entonces** veo el marcador de sets de cada partido jugado.
- **Dado** que la competición aún no ha empezado, **cuando** abro su clasificación, **entonces** veo los equipos con todo a cero.

## H07. Páginas del club

Como **persona interesada en apuntarse** quiero **conocer la historia del club, sus pabellones y cómo apuntarse** para **decidir si me uno y cómo contactar**.

**MoSCoW:** Should · **Puntos:** 2

**Diseño:** Páginas estáticas `/club`, `/club/instalaciones`, `/club/apuntate`.

**Criterios de aceptación**

- **Dado** que abro la página del club, **cuando** la leo, **entonces** veo la historia, el escudo y los enlaces a redes sociales.
- **Dado** que abro «Instalaciones», **cuando** la leo, **entonces** veo cada pabellón con su dirección.
- **Dado** que abro «Apúntate», **cuando** la leo, **entonces** veo las categorías con sus años de nacimiento y cómo contactar.

## H08. Páginas legales

Como **visitante** quiero **consultar la política de privacidad, el aviso legal y la política de cookies** para **saber qué se hace con mis datos**.

**MoSCoW:** Must · **Puntos:** 2

**Diseño:** Páginas estáticas `/privacidad`, `/aviso-legal`, `/cookies`, enlazadas en el pie.

**Criterios de aceptación**

- **Dado** que estoy en cualquier página, **cuando** miro el pie, **entonces** veo enlaces a privacidad, aviso legal y cookies.
- **Dado** que abro la política de privacidad, **cuando** la leo, **entonces** explica qué datos se guardan de los menores y quién da el consentimiento de imagen.

## H09. Iniciar sesión

Como **administrador** quiero **entrar en el panel con mi email y mi contraseña** para **gestionar el contenido de la web**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** Página `/login`; `POST /api/v1/auth/login`; BCrypt; access token JWT de 15 min en memoria; admin inicial desde `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

**Criterios de aceptación**

- **Dado** que la API arranca sin ningún administrador, **cuando** termina de arrancar, **entonces** existe uno con el email y la contraseña de las variables de entorno.
- **Dado** que introduzco email y contraseña correctos, **cuando** pulso «Entrar», **entonces** entro en `/gestion`.
- **Dado** que me equivoco de contraseña o el email no existe, **cuando** pulso «Entrar», **entonces** veo el mismo mensaje genérico en los dos casos y la API responde 401.
- **Dado** que no he iniciado sesión, **cuando** abro `/gestion`, **entonces** me lleva a `/login`.

## H10. Mantener y cerrar la sesión

Como **administrador** quiero **que la sesión siga abierta mientras trabajo y poder cerrarla** para **no perder lo que estoy haciendo y que nadie use mi sesión**.

**MoSCoW:** Must · **Puntos:** 8

**Diseño:** `POST /api/v1/auth/refresh` y `/logout`; refresh token en cookie `HttpOnly`, `SameSite=Strict`, rotación y detección de reutilización; tabla `refresh_tokens`.

**Criterios de aceptación**

- **Dado** que llevo una hora trabajando en el panel, **cuando** el access token está a punto de caducar, **entonces** se renueva sin que yo haga nada.
- **Dado** que dejo el panel 2 horas sin actividad, **cuando** vuelvo, **entonces** tengo que iniciar sesión de nuevo.
- **Dado** que han pasado 7 días desde que inicié sesión, **cuando** sigo trabajando, **entonces** tengo que iniciar sesión de nuevo.
- **Dado** que pulso «Cerrar sesión», **cuando** vuelvo a abrir `/gestion`, **entonces** me pide iniciar sesión, y el refresh token anterior ya no sirve.

## H11. Bloquear intentos repetidos de login

Como **administrador** quiero **que se frenen los intentos repetidos de adivinar mi contraseña** para **que nadie entre por fuerza bruta**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** Bucket4j y Caffeine en memoria; 5 fallos por email + IP bloquean 15 min; 20 peticiones por minuto por IP en `/api/v1/auth/**`.

**Criterios de aceptación**

- **Dado** que alguien falla 5 veces seguidas con mi email desde una IP, **cuando** lo intenta una sexta vez, **entonces** recibe 401 aunque la contraseña sea correcta, durante 15 minutos.
- **Dado** que esa IP está bloqueada para mi email, **cuando** entro desde otra IP con la contraseña correcta, **entonces** entro sin problema.
- **Dado** que una IP hace más de 20 peticiones en un minuto a `/api/v1/auth/**`, **cuando** hace la siguiente, **entonces** recibe 429.

## H12. Gestionar noticias

Como **administrador** quiero **crear, editar, publicar, retirar, destacar y borrar noticias** para **mantener informada a la gente del club**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** `/gestion/noticias`; `POST/PUT/PATCH/DELETE /api/v1/news`; borrador = sin `publishedAt`; borrado lógico.

**Criterios de aceptación**

- **Dado** que relleno título, resumen, tipo y cuerpo, **cuando** guardo, **entonces** la noticia queda como borrador y no se ve en la web pública.
- **Dado** que tengo un borrador, **cuando** lo publico, **entonces** aparece en la web con la fecha de publicación.
- **Dado** que una noticia está publicada, **cuando** la retiro, **entonces** desaparece de la web pública pero sigo viéndola en el panel.
- **Dado** que dejo el título vacío, **cuando** guardo, **entonces** veo el error junto al campo y no se guarda.

## H13. Editor Markdown de noticias

Como **administrador** quiero **escribir el cuerpo de la noticia en Markdown con vista previa y sin perderlo si se cierra la sesión** para **darle formato sin saber HTML y no perder trabajo**.

**MoSCoW:** Should · **Puntos:** 5

**Diseño:** Editor con vista previa; borrador en el almacenamiento del navegador (sin datos de sesión); saneado con OWASP Java HTML Sanitizer.

**Criterios de aceptación**

- **Dado** que escribo negritas, listas y enlaces en Markdown, **cuando** miro la vista previa, **entonces** se ven con formato.
- **Dado** que pego `<script>` en el cuerpo, **cuando** se publica la noticia, **entonces** el script no aparece en la página.
- **Dado** que se cierra la sesión mientras escribo, **cuando** vuelvo a entrar y abro el editor, **entonces** recupero el texto que tenía.

## H14. Subir imágenes

Como **administrador** quiero **subir la imagen de una noticia, la foto de una persona y el escudo de un equipo** para **que la web sea más visual**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** `PUT /api/v1/news/{id}/image`, `/people/{id}/photo`, `/teams/{id}/crest` (multipart); interfaz `ImageStorage`: disco en `dev`, Cloudinary en `prod`.

**Criterios de aceptación**

- **Dado** que elijo una imagen JPG o PNG de menos de 5 MB, **cuando** la subo, **entonces** se guarda y se ve en la web.
- **Dado** que elijo un fichero que no es una imagen o pesa más de 5 MB, **cuando** lo subo, **entonces** veo un error y no se guarda.
- **Dado** que subo la foto de una persona sin consentimiento de imagen, **cuando** se ve su ficha pública, **entonces** sigue saliendo el avatar genérico.

## H15. Temporadas, categorías e instalaciones

Como **administrador** quiero **gestionar las temporadas, las categorías y los pabellones** para **preparar la estructura antes de crear equipos y partidos**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** `/gestion/configuracion`; CRUD de `/api/v1/seasons`, `/categories`, `/venues`.

**Criterios de aceptación**

- **Dado** que creo la temporada 2026-27 y la marco como activa, **cuando** guardo, **entonces** es la única temporada activa.
- **Dado** que una categoría tiene equipos, **cuando** intento borrarla, **entonces** veo que no se puede porque tiene equipos (409).
- **Dado** que creo un pabellón con nombre y dirección, **cuando** guardo, **entonces** aparece al crear partidos y en la página de instalaciones.

## H16. Gestionar personas

Como **administrador** quiero **dar de alta jugadores y entrenadores con sus datos y su consentimiento de imagen** para **poder ponerlos en las plantillas**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** `/gestion/personas`; `GET /api/v1/people?search=&page=`, CRUD de `/api/v1/people`.

**Criterios de aceptación**

- **Dado** que relleno nombre, apellidos, fecha de nacimiento y consentimiento, **cuando** guardo, **entonces** la persona aparece en el buscador del panel.
- **Dado** que busco por parte del nombre, **cuando** escribo «luc», **entonces** veo a las personas cuyo nombre o apellidos lo contienen.
- **Dado** que borro una persona, **cuando** confirmo, **entonces** deja de aparecer, pero se conservan sus datos históricos (borrado lógico).

## H17. Gestionar equipos y plantillas

Como **administrador** quiero **crear los equipos de cada categoría y asignarles jugadores con dorsal y entrenadores** para **que la web muestre las plantillas correctas**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** `/gestion/equipos`; CRUD de `/api/v1/teams`; `POST/PATCH/DELETE /api/v1/teams/{id}/roster`.

**Criterios de aceptación**

- **Dado** que creo un equipo propio en una categoría y temporada, **cuando** guardo, **entonces** aparece en la página de equipos.
- **Dado** que añado un jugador con el dorsal 7, **cuando** guardo, **entonces** aparece en la plantilla con ese dorsal.
- **Dado** que intento poner a otro jugador el dorsal 7 en el mismo equipo, **cuando** guardo, **entonces** veo que el dorsal ya está ocupado (409).
- **Dado** que creo un equipo rival, **cuando** guardo, **entonces** no aparece en la página de equipos del club, pero sí en partidos y clasificaciones.

## H18. Gestionar competiciones y jornadas

Como **administrador** quiero **crear las ligas de cada categoría con sus jornadas** para **organizar el calendario de la temporada**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** `/gestion/competiciones`; CRUD de `/api/v1/competitions` y `/api/v1/matchdays`.

**Criterios de aceptación**

- **Dado** que creo una competición con temporada, categoría y puntos de sanción, **cuando** guardo, **entonces** aparece en la página de clasificaciones.
- **Dado** que añado las jornadas 1 a 10 con su fecha, **cuando** guardo, **entonces** puedo asignar partidos a cada jornada.

## H19. Gestionar partidos

Como **administrador** quiero **crear, editar, aplazar y cancelar partidos** para **que el calendario esté al día**.

**MoSCoW:** Must · **Puntos:** 5

**Diseño:** `/gestion/partidos`; CRUD de `/api/v1/matches`; estados `SCHEDULED`, `POSTPONED`, `CANCELLED`.

**Criterios de aceptación**

- **Dado** que creo un partido con jornada, local, visitante, fecha, hora y pabellón, **cuando** guardo, **entonces** aparece en el calendario.
- **Dado** que pongo el mismo equipo como local y visitante, **cuando** guardo, **entonces** veo un error y no se guarda.
- **Dado** que aplazo un partido, **cuando** miro el calendario público, **entonces** aparece como aplazado.

## H20. Introducir el marcador de sets

Como **administrador** quiero **introducir el resultado en sets de un partido jugado** para **que los aficionados vean cómo quedó**.

**MoSCoW:** Must · **Puntos:** 2

**Diseño:** `PATCH /api/v1/matches/{id}` con `homeSets` y `awaySets`; estado `FINISHED`. Los puntos por set llegan en la fase 3.

**Criterios de aceptación**

- **Dado** que un partido se ha jugado, **cuando** introduzco 3-1, **entonces** queda finalizado y se ve el resultado en el calendario y en la jornada.
- **Dado** que introduzco un marcador imposible (3-3 o 4-0), **cuando** guardo, **entonces** veo un error: uno de los dos tiene que llegar a 3 y el otro a menos de 3.

## H21. Editar la clasificación a mano

Como **administrador** quiero **corregir a mano la tabla de clasificación** para **publicarla mientras no se calcula sola (llega en la fase 3)**.

**MoSCoW:** Must · **Puntos:** 3

**Diseño:** `/gestion/competiciones/{id}/clasificacion`; `PUT /api/v1/competitions/{id}/standings`.

**Criterios de aceptación**

- **Dado** que una competición tiene equipos, **cuando** edito puntos, partidos y sets de cada uno, **entonces** la tabla pública se actualiza y se ordena por puntos, partidos ganados y cociente de sets.
- **Dado** que introduzco un número negativo, **cuando** guardo, **entonces** veo el error junto al campo y no se guarda.
