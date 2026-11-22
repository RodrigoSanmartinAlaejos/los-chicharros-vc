# Requisitos no funcionales

Última revisión: 2026-11-22.

Cómo de bien tiene que funcionar la web, con medidas que se puedan comprobar. Es el entregable del paso 6 de la planificación y vale para todo el proyecto; la columna «Desde» indica la fase en la que empieza a exigirse. El detalle técnico de cada regla está en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md) y el de las pruebas en la [guía de tests](../07-desarrollo/guia-tests.md).

## Rendimiento

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-01 | Lectura pública rápida con la web despierta | p95 de la API por debajo de 500 ms y p95 de las páginas SSR por debajo de 1 s, con 20 usuarios simultáneos | k6 contra `staging` antes de cada release | F1 |
| RNF-02 | Pocos errores bajo carga | Menos del 1 % de peticiones fallidas en la misma prueba | k6 | F1 |
| RNF-03 | Arranque en frío aceptable | La primera visita tras estar dormida carga en menos de 2 minutos | Monitor externo (tiempo de espera de 120 s) | F1 |
| RNF-04 | Páginas ligeras en móvil | Puntuación de rendimiento de Lighthouse (móvil) de al menos 80 en portada, noticias y clasificación | Lighthouse en Chrome, antes de cada release | F1 |
| RNF-05 | API dentro de la memoria del plan gratuito | Menos del 80 % de 512 MB (unos 410 MB) en uso normal | Panel de Render tras cada despliegue | F1 |

## Disponibilidad y copias

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-06 | Saber cuándo se cae | Aviso por email tras 2 fallos seguidos del monitor, que comprueba `prod` como mucho una vez por hora | Monitor externo | F1 |
| RNF-07 | Recuperar los datos | Copia diaria de TiDB (1 día) y exportación cifrada semanal y antes de cada release (30 días) | Restauración completa en local una vez por fase | F1 |
| RNF-08 | Despliegues seguros | Ningún despliegue a `prod` sin aprobación manual, migraciones correctas y smoke en verde | Workflow de despliegue y `scripts/smoke.sh` | F1 |

## Seguridad

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-09 | Contraseñas protegidas | BCrypt; nunca en logs ni en respuestas | Revisión de código y test de seguridad | F1 |
| RNF-10 | Sesión segura | Access token de 15 min solo en memoria; refresh token en cookie `HttpOnly`, `Secure`, `SameSite=Strict`, con rotación; sesión de 2 h sin actividad y 7 días como máximo | Tests de integración de autenticación | F1 |
| RNF-11 | Frenar la fuerza bruta | 5 fallos por email + IP bloquean 15 min; 20 peticiones por minuto por IP en `/api/v1/auth/**`; respuesta siempre 401 genérica | Tests de integración | F1 |
| RNF-12 | Control de acceso | Cada fila de la [matriz de permisos](matriz-permisos.md) tiene su test que devuelve 401 o 403 a quien no debe | Tests de seguridad (MockMvc) en la CI | F1 |
| RNF-13 | Contenido sin XSS | El HTML de las noticias se sanea en el backend con lista blanca; nunca `bypassSecurityTrustHtml` | Test con `<script>` en el cuerpo (H13) | F1 |
| RNF-14 | Ningún secreto en el repositorio | Secretos solo en variables de entorno; *push protection* activo | Secret scanning de GitHub | F0 |
| RNF-15 | Dependencias al día | Ninguna alerta de seguridad alta o crítica abierta más de 7 días | Alertas de Dependabot | F0 |
| RNF-16 | Revisión antes de publicar | OWASP Top 10 revisado en cada release | Checklist de la release | F1 |

## Privacidad (RGPD y menores)

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-17 | Datos mínimos en público | Las personas salen siempre con nombre e inicial del apellido; nunca fecha de nacimiento, teléfono ni email | Test de los DTOs públicos | F1 |
| RNF-18 | Fotos con consentimiento | Sin consentimiento de imagen se muestra un avatar genérico | Test de integración (H04, H14) | F1 |
| RNF-19 | Menores sin cuenta | Un menor nunca tiene `User`; lo gestiona su tutor | Restricción en el modelo ([ADR-003](../adr/003-person-user-split.md)) | F2 |
| RNF-20 | Derecho de acceso y supresión | Exportar los datos de una persona y anonimizarla en menos de 30 días desde la petición | Funcionalidad de la fase 2 y su test | F2 |
| RNF-21 | Logs sin datos personales | Ni contraseñas, ni tokens, ni datos de personas en logs ni en Sentry | Revisión de código | F1 |
| RNF-22 | Información legal | Páginas de privacidad, aviso legal y cookies enlazadas en el pie | H08 | F1 |

## Accesibilidad

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-23 | Nivel WCAG 2.1 AA | Contraste de al menos 4,5:1 en texto y 3:1 en elementos de interfaz ([paleta](paleta-colores.md)) | Paleta comprobada y Lighthouse | F1 |
| RNF-24 | Uso con teclado | Todo se puede usar con teclado, con el foco siempre visible | Prueba manual en cada pantalla nueva | F1 |
| RNF-25 | Puntuación de accesibilidad | Lighthouse (accesibilidad) de al menos 95 en las páginas públicas | Lighthouse antes de cada release | F1 |
| RNF-26 | Plantillas accesibles | Sin errores de `templateAccessibility` de angular-eslint | `frontend-lint` en la CI | F0 |

## Compatibilidad

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-27 | Navegadores | Las dos últimas versiones de Chrome, Edge, Firefox y Safari, y Chrome y Safari en móvil | Prueba manual antes de cada release | F1 |
| RNF-28 | Móvil primero | Todo se ve y se usa bien desde 360 px de ancho, sin desplazamiento horizontal | Herramientas de desarrollo y E2E | F1 |
| RNF-29 | Hora local | Las fechas se guardan en UTC y se muestran en la hora local, también en las páginas renderizadas en el servidor | Test de componentes | F1 |

## SEO

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-30 | Páginas públicas indexables | Las páginas públicas llegan renderizadas desde el servidor con título y descripción propios | Ver el HTML recibido sin JavaScript | F1 |
| RNF-31 | Vista previa al compartir | Las noticias llevan Open Graph (título, resumen e imagen) | Depurador de vista previa de una red social | F1 |
| RNF-32 | `staging` fuera de los buscadores | `noindex` y franja visible «Entorno de pruebas» | Revisión tras desplegar `staging` | F2 |

## Mantenibilidad

| Id | Requisito | Medida | Cómo se comprueba | Desde |
| --- | --- | --- | --- | --- |
| RNF-33 | Tests suficientes | Cobertura de al menos el 70 % en los servicios del backend | JaCoCo en la CI | F1 |
| RNF-34 | Nada entra sin CI | `main` y `develop` solo aceptan PR con las 4 comprobaciones en verde | Rulesets de GitHub | F0 |
| RNF-35 | Fácil de arrancar | Alguien nuevo lo tiene funcionando en menos de 15 minutos siguiendo el README | Prueba en un equipo limpio antes de la v1.0.0 | F5 |
| RNF-36 | Fácil de cambiar de proveedor | Toda la configuración entra por variables de entorno; nada atado al proveedor en el código | Revisión de código | F1 |

## Decisiones

La mayoría de las medidas salen del plan, las pautas, las reglas técnicas y la guía de tests. Estas se añadieron al redactar el documento y se confirmaron el 2026-11-22:

- **RNF-04 y RNF-25:** puntuaciones mínimas de Lighthouse (80 en rendimiento y 95 en accesibilidad).
- **RNF-15:** 7 días como máximo para una alerta de seguridad alta o crítica.
- **RNF-20:** 30 días para atender una petición de acceso o supresión (es el plazo general del RGPD).
- **RNF-27 y RNF-28:** navegadores soportados y ancho mínimo de 360 px.
