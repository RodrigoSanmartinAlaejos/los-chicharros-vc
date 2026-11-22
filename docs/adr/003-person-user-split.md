# ADR-003: `Person` separada de `User`; la cuenta es opcional

Última revisión: 2026-11-22.

- **Estado:** Aceptado
- **Fecha:** 2026-11-10

## Contexto

El club tiene jugadores menores. Un menor no tiene email propio ni debe tener login: lo gestiona su tutor, que sí tiene cuenta. Además hay entrenadores o jugadores que no van a usar la web, pero tienen que aparecer en plantillas, convocatorias y resultados. Por otro lado, el administrador inicial es una cuenta técnica que no es ninguna persona del club.

## Decisión

`Person` guarda los datos de una persona (jugador, entrenador, tutor) y `User` es solo su cuenta de acceso. La relación es opcional en los dos sentidos: una `Person` puede no tener `User` (un menor) y un `User` puede no tener `Person` (el administrador inicial). Pertenencias, tutelas, convocatorias, asistencias y cuotas apuntan siempre a `Person`, nunca a `User`.

## Alternativas descartadas

- **Una sola entidad `User` para todos:** obligaría a crear cuentas falsas o con email del tutor para los menores, y mezclaría datos personales con credenciales.
- **Entidad `Player` aparte, migrada a `User` en la fase 2:** una migración de datos innecesaria y un modelo que habría que tirar.

## Consecuencias

- Los menores no pueden iniciar sesión; lo que les afecta lo hace su tutor a través de `Guardianship`.
- Las comprobaciones de propiedad («¿es su menor?», «¿es su equipo?») pasan siempre por la `Person` de la cuenta.
- Una cuenta sin `Person` no puede tener pertenencias, tutelas, convocatorias ni cuotas; si esa persona entra en el club, se vincula su cuenta a su `Person`.
- El borrado por RGPD anonimiza la `Person` y conserva resultados, asistencias y pagos.
- Las equivalencias y las reglas están en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md) y los permisos en la [matriz de permisos](../02-requisitos/matriz-permisos.md).
