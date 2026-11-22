# Visión y alcance

Última revisión: 2026-11-22.

Los Chicharros V.C. es la web completa de un club de voleibol: una parte pública para aficionados y familias, y áreas privadas para que jugadores, tutores, entrenadores y la junta gestionen el día a día. El club es ficticio, pero la web se diseña como si fuera para uno real.

## Problema

Un club de voleibol amateur suele repartir su información entre redes sociales, grupos de mensajería, hojas de cálculo y la web de la federación:

- Las familias no tienen un sitio único donde ver el calendario, los resultados y la clasificación de su equipo.
- Los entrenadores convocan y pasan lista por mensajería, sin registro de quién confirmó o asistió.
- Los resultados y la clasificación se actualizan a mano y con retraso.
- El control de cuotas depende de una persona y de su hoja de cálculo.
- Hay datos de menores circulando por canales sin control de acceso ni consentimiento de imagen.

## Objetivos

El proyecto tiene dos objetivos, y las decisiones se toman pensando en los dos:

| Objetivo | Qué significa | Qué exige al proyecto |
| --- | --- | --- |
| **Portfolio para buscar trabajo** | Demostrar que sé llevar un proyecto fullstack como en una empresa | Código limpio y probado, CI, PRs revisados, ADR y documentación que se pueda enseñar y explicar en una entrevista |
| **Presentarlo a un club real** | Que un club pueda adoptarlo cambiando datos y configuración, no reescribiéndolo | Datos de ejemplo sustituibles, reglas de competición configurables, protección de menores y RGPD desde el diseño, coste de alojamiento 0 € |

## Usuarios

| Rol | Qué necesita de la web |
| --- | --- |
| Visitante | Noticias, equipos, calendario, clasificación y cómo apuntarse |
| Socio / jugador | Sus equipos, partidos, convocatorias, avisos y cuotas |
| Tutor | Gestionar a sus hijos menores, que no tienen cuenta propia |
| Entrenador | Plantilla, convocatorias, asistencia y resultados de sus equipos |
| Coordinador deportivo | Supervisar todos los equipos, el calendario y los resultados |
| Comunicación | Publicar noticias y avisos del club |
| Tesorero | Cuotas, pagos e informes, sin acceso a la gestión deportiva |
| Administrador | Usuarios, roles, temporadas, categorías e instalaciones |

Qué puede hacer cada rol está en la [matriz de permisos](../02-requisitos/matriz-permisos.md).

## Alcance

El proyecto avanza por fases; cada una termina con una versión que se puede enseñar. El detalle está en el [plan del proyecto](../00-plan/plan-proyecto.md).

| Fase | Versión | Qué aporta |
| --- | --- | --- |
| 1. MVP público | v0.1.0 | Web pública desplegada (noticias, equipos, calendario, clasificación) y panel del administrador |
| 2. Autenticación y roles | v0.2.0 | Cuentas, invitaciones, roles, tutores y áreas privadas |
| 3. Funciones de equipo | v0.3.0 | Convocatorias, entrenamientos, resultados set a set y clasificación automática |
| 4. Extras e integraciones | v0.4.0 | Emails, calendario iCal, mapa de pabellones, cuotas y pago online |
| 5. Pulido y despliegue final | v1.0.0 | Calidad, accesibilidad, SEO y material para el portfolio |

### Fuera de alcance

- App móvil nativa: la web es responsive, pensada primero para móvil.
- Retransmisión de partidos en directo.
- Tienda online.
- Gestión de varios clubes en una misma instalación.
- Integración automática con la web de la federación: los resultados de otros equipos se introducen a mano.

## Restricciones

- **Presupuesto:** 0 €. Render y TiDB Cloud Starter en planes gratuitos, sin tarjeta y sin dominio propio por ahora.
- **Tiempo:** 5-10 horas semanales, compatibles con las prácticas; sin fechas, por orden de fases.
- **Equipo:** una sola persona, trabajando como si fuera un equipo (issues, ramas, PRs y CI).
- **Datos:** siempre ficticios, nunca de personas reales, ni en desarrollo ni en la demo.

## Criterios de éxito

| Para | Criterio |
| --- | --- |
| Portfolio | La web está online con HTTPS y la CI en verde en `main` |
| Portfolio | Cualquiera clona el repositorio y lo arranca en menos de 15 minutos siguiendo el README |
| Portfolio | Cobertura de tests de al menos el 70 % en los servicios del backend |
| Portfolio | Hay usuarios de demo por rol y una explicación de 2 minutos del problema, la solución y las decisiones |
| Club real | Un socio recibe su convocatoria y su recordatorio de cuota por email y se suscribe al calendario de su equipo |
| Club real | La clasificación pública cambia sola al introducir un resultado |
| Club real | Ningún dato privado de un menor es visible sin sesión, y su foto solo aparece con consentimiento de imagen |
| Club real | Cambiar de club solo exige sustituir datos y configuración, no tocar el código |
