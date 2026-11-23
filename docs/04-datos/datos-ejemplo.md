# Datos de ejemplo de la temporada 2026-27

Última revisión: 2026-11-23.

Datos **ficticios** para desarrollo (`dev`), pruebas (`staging`) y la demo: equipos, personas, competiciones, jornadas, partidos, clasificaciones y noticias de la temporada 2026-27. Siguen la [ficha del club](../01-vision/club.md) y las columnas del [diccionario de datos de la fase 1](diccionario-fase-1.md). Ninguna persona es real y no hay fotos de personas (regla de las [pautas](../00-plan/pautas-proyecto.md)).

## Cómo se generan

Los CSV de [`datos-ejemplo/`](datos-ejemplo/) los escribe un script, siempre con el mismo resultado porque usa un generador pseudoaleatorio con semilla fija:

```sh
node scripts/datos-ejemplo/generar.mjs                 # corte por defecto: 2026-12-20
node scripts/datos-ejemplo/generar.mjs --corte=2027-02-01
```

- **Fecha de corte:** los partidos anteriores salen jugados (`FINISHED`) con su marcador. Los posteriores salen programados (`SCHEDULED`) y sin marcador. La clasificación y las crónicas se calculan con los jugados. Antes de una demo se puede volver a generar con una fecha de corte cercana.
- **No se editan a mano:** si hay que cambiar algo, se cambia el script y se vuelve a ejecutar, en el mismo PR.
- **De CSV a semilla:** cuando existan las migraciones de la fase 1, la semilla `db/seed` se escribe a partir de estos ficheros. Los CSV usan códigos legibles (`CADETE_F-LCV`, `P012`) en lugar de ids. Al pasar a SQL, cada código se convierte en una subconsulta o una variable, para no depender de los `AUTO_INCREMENT`.

## Ficheros

Uno por tabla del diccionario, en el orden de las migraciones. No hay `users` ni `refresh_tokens`: en la fase 1 el único usuario es el administrador, que se crea al arrancar desde `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

| Fichero | Filas | Qué contiene |
| --- | --- | --- |
| `seasons.csv` | 1 | Temporada 2026-27, del 1 de septiembre al 30 de junio, activa |
| `categories.csv` | 10 | Alevín, infantil, cadete, juvenil y sénior, masculina y femenina, con su orden |
| `venues.csv` | 2 | Pabellón del Puerto y Polideportivo La Marina, con coordenadas aproximadas en Santa Cruz |
| `competitions.csv` | 10 | Una liga insular por categoría y sexo; federación ficticia y 1 punto de sanción por incomparecencia |
| `teams.csv` | 68 | 10 del club (`own_club = true`) y 58 rivales: los cinco primeros clubes de la ficha en las ligas de 6 y los siete en las de 8 |
| `people.csv` | 124 | 116 jugadores y 8 entrenadores |
| `team_memberships.csv` | 128 | Plantillas de los equipos del club con dorsal y posición, y el entrenador de cada equipo |
| `matchdays.csv` | 116 | Jornadas de cada liga (10 en las de 6 equipos, 14 en las de 8) |
| `matches.csv` | 404 | Todos los partidos de todas las ligas, también los que se juegan entre rivales |
| `standings.csv` | 68 | Clasificación de cada liga a la fecha de corte, ordenada por puntos y diferencia de sets |
| `news_articles.csv` | 8 | Noticias publicadas de los tres tipos y un borrador |

## Reglas que siguen los datos

**Calendario**

- **Jornadas:** cada dos semanas desde el sábado 3 de octubre de 2026, sin jornada el 26 de diciembre. Las ligas de 8 equipos acaban en abril y las de 6, en febrero.
- **Emparejamientos:** liga a doble vuelta por el método del círculo. En la segunda vuelta se invierten local y visitante.

**Partidos**

- **Pabellones:** los partidos en casa del club se juegan en su pabellón: la cantera (alevín, infantil y cadete) en La Marina y juvenil y sénior en el Puerto. Cada partido tiene su propio horario, sin coincidencias en el mismo pabellón. En los partidos entre rivales y fuera de casa, `venue` va vacío (diccionario: pabellón opcional).
- **Horas:** se eligen en hora de Canarias y se guardan en UTC (`starts_at`). Hasta el 25 de octubre de 2026 rige el horario de verano (UTC+1) y desde esa fecha, UTC+0.
- **Resultados:** a mejor nivel del equipo, más probable que gane cada set, con una pequeña ventaja para el que juega en casa. Todos los marcadores cumplen `ck_matches_sets`: uno llega a 3 y el otro se queda por debajo.
- **Aplazamiento:** hay un partido aplazado (`POSTPONED`) entre dos rivales en la jornada 2 de la liga cadete masculina, para que la demo tenga ese estado.

**Clasificación**

- **Puntos:** 3-0 y 3-1 dan 3 puntos al ganador y 0 al perdedor; 3-2 da 2 y 1 ([plan](../00-plan/plan-proyecto.md)).
- **Sanciones:** no hay ninguna, así que `penalty_points` queda en 0.

**Personas**

- **Edades:** los jugadores nacen dentro de los años de su categoría (ficha del club). Los sénior, entre 1990 y 2008.
- **Nombres:** combinan nombres y apellidos frecuentes en Canarias, sin repetir persona.
- **Equipos dobles:** dos juveniles, uno por sexo, juegan también en el sénior.
- **Entrenadores:** son adultos sin fecha de nacimiento (opcional en el diccionario). El de alevín entrena al masculino y al femenino, y el de infantil, igual.
- **Fotos:** `photo_url` va vacío en todas las personas. La web muestra la silueta [`persona.svg`](../assets/imagenes/persona.svg).
- **Consentimiento de imagen:** `image_consent` está activado en un 70 % de los menores y en un 85 % de los adultos, para probar las dos situaciones.
- **Dorsales y posiciones:** dorsales únicos por equipo. En alevín (minivoley) no hay posición. En el resto de categorías hay 2 colocadores, 2 opuestos, 3 centrales, 3 receptores y 2 líberos.

**Noticias**

- **Contenido:** dos de principio de temporada (horarios y presentación de equipos), cuatro crónicas de partidos que existen en `matches.csv` y el campus de Navidad. El borrador lleva `published_at` vacío.
- **Crónicas:** no nombran a ningún jugador, para no exponer a menores.
- **Imágenes:** las de [`docs/assets/imagenes/`](../assets/imagenes/README.md), servidas desde `/assets/imagenes/`.

## Equipos del club a la fecha de corte

Resultado del script con el corte por defecto (jornada 6 jugada en todas las ligas):

| Liga | Puesto | Puntos | Ganados-perdidos |
| --- | --- | --- | --- |
| Liga Insular Alevín masculina | 3.º de 6 | 10 | 3-3 |
| Liga Insular Alevín femenina | 3.º de 6 | 9 | 4-2 |
| Liga Insular Infantil masculina | 5.º de 6 | 5 | 1-5 |
| Liga Insular Infantil femenina | 4.º de 6 | 9 | 3-3 |
| Liga Insular Cadete masculina | 1.º de 8 | 15 | 5-1 |
| Liga Insular Cadete femenina | 1.º de 8 | 13 | 4-2 |
| Liga Insular Juvenil masculina | 5.º de 6 | 6 | 2-4 |
| Liga Insular Juvenil femenina | 3.º de 6 | 9 | 3-3 |
| Liga Insular Sénior masculina | 2.º de 8 | 12 | 4-2 |
| Liga Insular Sénior femenina | 1.º de 8 | 16 | 5-1 |
