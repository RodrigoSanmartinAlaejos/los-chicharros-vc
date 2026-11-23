# Plan de sprints de la fase 1

Última revisión: 2026-11-23.

Cómo se reparte el trabajo de la fase 1 (MVP público) en sprints de dos semanas, sin fechas. Es el entregable del paso 12 de la planificación y cierra la fase 0. Las historias están en [historias de la fase 1](../02-requisitos/historias-fase-1.md); las tareas técnicas, que no son historias, se crean como issues con la plantilla de tarea técnica.

## Capacidad

- Dedicación: de 5 a 10 horas por semana, es decir, de 10 a 20 horas por sprint.
- Estimación inicial: **1 punto ≈ 1,5 horas**, así que caben **de 7 a 13 puntos por sprint**; el plan apunta a la parte alta y se recorta si no da tiempo.
- La estimación se recalibra al cerrar el sprint 1 con la velocidad real (puntos terminados), y el plan se ajusta en la retrospectiva.
- Total de la fase: 21 historias y 15 tareas técnicas, **124 puntos** en 11 sprints.

## Sprints

| Sprint | Objetivo | Trabajo | Puntos |
| --- | --- | --- | --- |
| Sprint 0 | **Herramientas de calidad y datos de ejemplo.** Cierra la fase 0: formato y cobertura automáticos y los datos para la demo. | #83 Spotless y Checkstyle en el backend (2)<br>#84 Husky y lint-staged (pre-commit) (2)<br>#85 JaCoCo y cobertura en la CI (1)<br>#86 Datos de ejemplo e imágenes libres (3) | 8 |
| Sprint 1 | **Bases del backend y del frontend.** Esqueleto de todo lo demás y la primera pantalla de configuración. | #87 Base del backend: errores, auditoría y tests de integración (3)<br>#88 Base del frontend: tokens, fuentes, layout y navegación (3)<br>#89 Proxy de /api en el SSR y configuración por entorno (3)<br>#91 Actuator y despertar de la API desde el SSR (1)<br>#59 Temporadas, categorías e instalaciones (3) | 13 |
| Sprint 2 | **Login y sesión.** El administrador entra, trabaja sin perder la sesión y la cierra. | #53 Iniciar sesión (5)<br>#54 Mantener y cerrar la sesión (8) | 13 |
| Sprint 3 | **Personas y equipos.** Seguridad del login y alta de la estructura del club. | #55 Bloquear intentos repetidos de login (3)<br>#60 Gestionar personas (3)<br>#61 Gestionar equipos y plantillas (5)<br>#90 Swagger con springdoc (1) | 12 |
| Sprint 4 | **Competiciones, partidos y resultados.** Todo el calendario y la clasificación se gestionan desde el panel. | #62 Gestionar competiciones y jornadas (3)<br>#63 Gestionar partidos (5)<br>#64 Introducir el marcador de sets (2)<br>#65 Editar la clasificación a mano (3) | 13 |
| Sprint 5 | **Noticias e imágenes.** Publicar noticias con imagen y comprobar cada despliegue. | #56 Gestionar noticias (5)<br>#58 Subir imágenes (5)<br>#92 Smoke tras desplegar (2) | 12 |
| Sprint 6 | **Editor y portada.** Editor Markdown y la primera pantalla pública. | #57 Editor Markdown de noticias (5)<br>#45 Portada con lo último del club (5)<br>#46 Listado de noticias (3) | 13 |
| Sprint 7 | **Noticia, equipos y calendario públicos.** La web pública casi completa. | #47 Detalle de noticia (3)<br>#48 Equipos y su plantilla (5)<br>#49 Calendario de partidos (5) | 13 |
| Sprint 8 | **Clasificación, club y pruebas de la release.** Web pública completa y E2E. | #50 Clasificación y resultados (5)<br>#51 Páginas del club (2)<br>#52 Páginas legales (2)<br>#93 Playwright y E2E de la release (3) | 12 |
| Sprint 9 | **Despliegue.** La web online en Render y TiDB, con despliegue seguro. | #94 Primer despliegue en Render y TiDB Cloud Starter (5)<br>#95 Workflow de despliegue a producción (5) | 10 |
| Sprint 10 | **Copias, monitor y release v0.1.0.** Copias, vigilancia y la primera versión publicada; el margen sobrante es para arreglos de la release. | #96 Copias de seguridad de club_prod (3)<br>#97 Monitor de caídas y memoria de la API (2) | 5 |

El orden sigue las dependencias: primero las bases y el login, después lo que el panel necesita para tener datos (configuración, personas, equipos, competiciones, partidos), luego las noticias y, al final, las páginas públicas que muestran todo eso y el despliegue. Cada sprint termina con algo que se puede enseñar en la demo.

## Cómo se lleva en GitHub

- Cada historia y tarea técnica es un issue del milestone «Fase 1: MVP público», salvo las del sprint 0, que van en «Fase 0: Preparación».
- El tablero tiene dos campos más: **Sprint** (de «Sprint 0» a «Sprint 10») y **Puntos**. Se filtra la vista por el sprint en curso.
- Al empezar un sprint: se escribe su objetivo en el tablero y sus issues pasan de Backlog a **Listo**.
- Al terminarlo: demo (captura o vídeo corto), nota de qué mejorar y, si dos sprints seguidos no cumplen su objetivo, se recorta alcance de la fase en lugar de alargarla (pautas).

## Tareas técnicas

| Id | Tarea | Puntos | Sprint |
| --- | --- | --- | --- |
| T01 | #83 Spotless y Checkstyle en el backend | 2 | Sprint 0 |
| T02 | #84 Husky y lint-staged (pre-commit) | 2 | Sprint 0 |
| T03 | #85 JaCoCo y cobertura en la CI | 1 | Sprint 0 |
| T04 | #86 Datos de ejemplo e imágenes libres | 3 | Sprint 0 |
| T05 | #87 Base del backend: errores, auditoría y tests de integración | 3 | Sprint 1 |
| T06 | #88 Base del frontend: tokens, fuentes, layout y navegación | 3 | Sprint 1 |
| T07 | #89 Proxy de /api en el SSR y configuración por entorno | 3 | Sprint 1 |
| T08 | #90 Swagger con springdoc | 1 | Sprint 3 |
| T09 | #91 Actuator y despertar de la API desde el SSR | 1 | Sprint 1 |
| T10 | #92 Smoke tras desplegar | 2 | Sprint 5 |
| T11 | #93 Playwright y E2E de la release | 3 | Sprint 8 |
| T12 | #94 Primer despliegue en Render y TiDB Cloud Starter | 5 | Sprint 9 |
| T13 | #95 Workflow de despliegue a producción | 5 | Sprint 9 |
| T14 | #96 Copias de seguridad de club_prod | 3 | Sprint 10 |
| T15 | #97 Monitor de caídas y memoria de la API | 2 | Sprint 10 |

## Decisiones

- **1 punto ≈ 1,5 horas** como punto de partida, a recalibrar tras el sprint 1.
- **Sprint 0** para cerrar la fase 0 con las herramientas de calidad (Spotless, Checkstyle, Husky, JaCoCo) y los datos de ejemplo antes de escribir código de negocio.
- **Sprint y puntos como campos del tablero**, no como etiquetas: un campo de selección «Sprint» y un campo numérico «Puntos».
- **El despliegue va al final**, en los sprints 9 y 10; hasta entonces la demo de cada sprint se hace en local. El sprint 10 es corto a propósito, para dejar margen a los arreglos de la release.
