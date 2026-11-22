# Glosario

Última revisión: 2026-11-22.

Términos del dominio del club y su nombre en el código. Cada palabra tiene un solo significado en todo el proyecto: si aparece un término nuevo, se añade aquí en el mismo PR. Las tablas, rutas y reglas de nombres están en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md).

## Club y temporada

| Término | Significado | En el código |
| --- | --- | --- |
| Temporada | Periodo de competición, de septiembre a junio (2026-27). Solo una está activa | `Season` |
| Categoría | Grupo de edad: alevín, infantil, cadete, juvenil o sénior, masculina o femenina. Solo se usa con este sentido | `Category` |
| Equipo | Equipo de una categoría en una temporada; puede ser del club o rival | `Team` (`ownClub`) |
| Instalación | Pabellón donde se juega o se entrena | `Venue` |

## Personas y cuentas

| Término | Significado | En el código |
| --- | --- | --- |
| Persona | Jugador, entrenador o tutor, tenga o no cuenta | `Person` |
| Usuario | Cuenta de acceso a la web; opcional para una persona ([ADR-003](../adr/003-person-user-split.md)) | `User` |
| Rol | Lo que una cuenta puede hacer: socio, tutor, entrenador, coordinador, comunicación, tesorero o administrador | `Role` |
| Socio | Persona con cuenta que juega en el club o es miembro | Rol `MEMBER` |
| Tutor | Adulto con cuenta que gestiona a uno o varios menores | Rol `GUARDIAN` |
| Tutela | Relación entre un tutor y un menor | `Guardianship` |
| Menor | Persona de menos de 18 años; no tiene cuenta propia | `Person` sin `User` |
| Pertenencia | Relación de una persona con un equipo, como jugador o entrenador | `TeamMembership` |
| Plantilla | Conjunto de pertenencias de un equipo | `/teams/{id}/roster` |
| Dorsal | Número de camiseta de un jugador en un equipo | `shirtNumber` |
| Consentimiento de imagen | Permiso para mostrar la foto de una persona; si es menor, lo da su tutor | `imageConsent` |

## Competición

| Término | Significado | En el código |
| --- | --- | --- |
| Competición | Liga de una categoría en una temporada | `Competition` |
| Jornada | Ronda de una competición en la que cada equipo juega como mucho un partido | `Matchday` |
| Partido | Encuentro entre un equipo local y uno visitante | `Match` |
| Set | Parte de un partido que gana el primero en llegar a 25 puntos (15 en el quinto) con 2 de ventaja | `MatchSet` |
| Marcador de sets | Resultado del partido en sets (3-1); gana quien llega a 3 | `homeSets`, `awaySets` |
| Incomparecencia | El equipo no se presenta: pierde 3-0 (25-0 por set) y se le restan puntos de sanción | Estado `FORFEITED` |
| Aplazado / cancelado | Partido que no se juega en su fecha / que no se juega; no suman puntos | `POSTPONED`, `CANCELLED` |
| Clasificación | Tabla de una competición: puntos, partidos, sets y puntos de sanción | `Standing` |
| Puntos de sanción | Puntos que se restan a un equipo por incomparecencia | `penaltyPoints` |
| Cociente de sets | Sets ganados entre sets perdidos; tercer criterio de desempate | Calculado |

## Día a día del equipo

| Término | Significado | En el código |
| --- | --- | --- |
| Convocatoria | Llamada de un entrenador a un jugador para un partido, con su respuesta | `CallUp` |
| Entrenamiento | Sesión de entrenamiento de un equipo en una instalación | `TrainingSession` |
| Asistencia | Si un jugador fue a un entrenamiento y, si no, el motivo | `Attendance` |

## Comunicación y cuotas

| Término | Significado | En el código |
| --- | --- | --- |
| Noticia | Publicación de la parte pública: crónica, evento o comunicado | `NewsArticle` |
| Crónica / evento / comunicado | Tipos de noticia | `MATCH_REPORT`, `EVENT`, `STATEMENT` |
| Aviso | Mensaje privado al club, a un equipo o a una persona. Solo se usa con este sentido | `Announcement` |
| Cuota | Importe que debe pagar una persona en una temporada | `MembershipFee` |
| Pago | Abono de una cuota, online o manual | `Payment` |
