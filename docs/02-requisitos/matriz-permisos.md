# Matriz de permisos: rol × acción

Última revisión: 2026-11-12 (primera versión: 2026-11-10). Es el entregable del paso 3 de la fase de planificación.

Esta matriz es la referencia de autorización del proyecto: cada fila acaba siendo una regla `@PreAuthorize`, una comprobación de propiedad en el servicio y un test de seguridad (401/403). Si una tabla de endpoints del [plan](../00-plan/plan-proyecto.md) dice otra cosa, manda esta matriz.

## Cómo leerla

| Símbolo | Significado |
| --- | --- |
| ✔ | Puede hacerlo sobre cualquier registro |
| ● | Solo sobre lo propio: sus datos, sus equipos o sus menores a cargo |
| — | No puede |

Columnas: **Vis** visitante (sin sesión), **Soc** socio / jugador, **Tut** tutor, **Ent** entrenador, **Coo** coordinador deportivo, **Com** comunicación, **Tes** tesorero, **Adm** administrador.

Reglas generales:

- Los permisos se suman: quien tiene varios roles puede hacer lo que permita cualquiera de ellos.
- Todo lo que puede el visitante lo puede cualquier usuario con sesión.
- "Lo propio" se comprueba siempre en el backend: el entrenador contra los equipos donde tiene una pertenencia `COACH`, el tutor contra sus tutelas, el socio contra su propia persona.
- Los menores no tienen cuenta: lo que les afecta lo hace su tutor.
- La columna **Fase** indica cuándo se implementa la acción. En la fase 1 solo existe el administrador.

## Contenido público

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Ver noticias, equipos, calendario, clasificación y páginas del club | 1 | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |
| Suscribirse al calendario iCal de un equipo | 4 | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ | ✔ |

## Noticias y avisos

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Crear, editar, publicar y retirar noticias | 1 | — | — | — | — | — | ✔ | — | ✔ |
| Subir imágenes de noticias | 1 | — | — | — | — | — | ✔ | — | ✔ |
| Publicar un aviso general del club | 2 | — | — | — | — | ✔ | ✔ | — | ✔ |
| Publicar un aviso a un equipo | 2 | — | — | — | ● | ✔ | — | — | ✔ |
| Publicar un aviso a una persona | 2 | — | — | — | ● | ✔ | — | ✔ | ✔ |
| Ver los avisos privados que le corresponden | 2 | — | ● | ● | ● | ● | ● | ● | ● |

## Equipos y plantillas

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Crear, editar y borrar equipos | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Dar de alta personas (jugadores y entrenadores) | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Añadir o quitar jugadores de un equipo | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Asignar entrenadores a equipos | 2 | — | — | — | — | ✔ | — | — | ✔ |
| Ver la plantilla con datos privados (apellidos, teléfono, tutor) | 2 | — | — | — | ● | ✔ | — | — | ✔ |
| Editar datos deportivos de un jugador (dorsal, posición) | 2 | — | — | — | ● | ✔ | — | — | ✔ |
| Subir foto de un jugador o escudo de un equipo | 1 | — | — | — | ● | ✔ | ✔ | — | ✔ |

## Competición, calendario y resultados

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Crear y editar competiciones y jornadas | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Crear, editar y borrar partidos | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Aplazar o cancelar un partido | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Introducir el marcador de sets de un partido (3-1), sin puntos por set | 1 | — | — | — | — | ✔ | — | — | ✔ |
| Introducir el resultado set a set, con validación y recálculo de la clasificación | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Corregir un resultado ya finalizado | 3 | — | — | — | — | ✔ | — | — | ✔ |
| Registrar una incomparecencia | 3 | — | — | — | — | ✔ | — | — | ✔ |
| Introducir resultados de partidos entre rivales | 3 | — | — | — | — | ✔ | — | — | ✔ |
| Editar la clasificación a mano (solo hasta la fase 3) | 1 | — | — | — | — | — | — | — | ✔ |

## Convocatorias y entrenamientos

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Convocar jugadores a un partido | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Responder a una convocatoria | 3 | — | ● | ● | — | — | — | — | — |
| Ver las respuestas de una convocatoria | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Gestionar el horario de entrenamientos | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Pasar lista en un entrenamiento | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Ver estadísticas de asistencia del equipo | 3 | — | — | — | ● | ✔ | — | — | ✔ |
| Ver su propia asistencia y convocatorias | 3 | — | ● | ● | — | — | — | — | — |

## Perfil y menores

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Ver y editar su perfil y cambiar su contraseña | 2 | — | ● | ● | ● | ● | ● | ● | ● |
| Ver sus equipos y próximos partidos | 2 | — | ● | ● | ● | ● | ● | ● | ● |
| Ver y editar los datos de sus menores a cargo | 2 | — | — | ● | — | — | — | — | ✔ |
| Dar o retirar el consentimiento de imagen | 2 | — | ● | ● | — | — | — | — | ✔ |
| Pedir la exportación o el borrado de sus datos | 2 | — | ● | ● | ● | ● | ● | ● | ● |

## Cuotas y pagos

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Ver sus cuotas y su estado | 4 | — | ● | ● | — | — | — | — | — |
| Pagar una cuota online | 4 | — | ● | ● | — | — | — | — | — |
| Crear, editar y anular cuotas | 4 | — | — | — | — | — | — | ✔ | ✔ |
| Registrar un pago manual (efectivo, transferencia) | 4 | — | — | — | — | — | — | ✔ | ✔ |
| Ver el estado de cuotas de todos los socios | 4 | — | — | — | — | — | — | ✔ | ✔ |
| Enviar recordatorios de pago | 4 | — | — | — | — | — | — | ✔ | ✔ |
| Exportar listados de socios e informes económicos | 4 | — | — | — | — | — | — | ✔ | ✔ |
| Exportar listados de asistencia | 4 | — | — | — | ● | ✔ | — | — | ✔ |

## Administración

| Acción | Fase | Vis | Soc | Tut | Ent | Coo | Com | Tes | Adm |
| --- | --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Entrar al panel de gestión (`/gestion`); cada rol ve solo sus secciones | 1 (admin), 2 (resto) | — | — | — | — | ✔ | ✔ | ✔ | ✔ |
| Entrar a la configuración: usuarios, roles, temporadas, categorías, instalaciones e historial | 1 | — | — | — | — | — | — | — | ✔ |
| Crear, desactivar e invitar usuarios | 2 | — | — | — | — | — | — | — | ✔ |
| Asignar y quitar roles | 2 | — | — | — | — | — | — | — | ✔ |
| Registrar tutelas (tutor de un menor) | 2 | — | — | — | — | ✔ | — | — | ✔ |
| Gestionar temporadas, categorías e instalaciones | 1 | — | — | — | — | — | — | — | ✔ |
| Consultar el historial de cambios | 3 | — | — | — | — | — | — | — | ✔ |
| Atender una petición de exportación o borrado de datos | 2 | — | — | — | — | — | — | — | ✔ |

El panel de gestión tiene una sección por área: noticias y avisos (comunicación), equipos, personas, competiciones y partidos (coordinador), cuotas y pagos (tesorero, fase 4) y configuración (solo admin). El menú muestra solo las secciones del rol, pero el backend comprueba cada acción. El entrenador no entra al panel: trabaja desde su área de entrenador.

## Puntos que conviene revisar

Son decisiones que he tomado al redactar la matriz y que no estaban escritas en el plan:

- **Corregir un resultado finalizado** queda para coordinador y admin; el entrenador solo lo introduce la primera vez.
- **El tesorero no ve datos deportivos** (plantillas, asistencia) ni el coordinador datos económicos.
- **El entrenador no da de alta jugadores** ni los mueve de equipo: lo hace el coordinador.
- **Comunicación puede subir fotos** de jugadores y escudos, además de las de noticias; el sistema solo las muestra si hay consentimiento de imagen.
- **El historial de cambios** lo consulta solo el administrador.
