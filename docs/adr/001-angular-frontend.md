# ADR-001: Frontend con Angular

Última revisión: 2026-11-22.

- **Estado:** Aceptado
- **Fecha:** 2026-11-03

## Contexto

El proyecto es una web de portfolio fullstack con un backend en Spring Boot. El frontend tiene una parte pública y varias áreas privadas por rol, con muchos formularios de gestión (noticias, partidos, resultados, convocatorias, cuotas). Lo desarrolla una sola persona, en sesiones cortas, y tiene que poder continuarlo otra.

## Decisión

El frontend se hace con Angular, con TypeScript en modo estricto y componentes *standalone*.

## Alternativas descartadas

- **React (con Next.js):** es una librería y cada pieza (rutas, formularios, peticiones HTTP) se elige aparte; más decisiones y más dependencias que mantener para una sola persona.
- **Vue (con Nuxt):** más ligero, pero con menos presencia en las ofertas de empresa a las que va dirigido el portfolio.

## Consecuencias

- Angular trae de serie lo que el proyecto necesita: rutas con carga diferida, formularios reactivos tipados, cliente HTTP con interceptores, inyección de dependencias y SSR.
- Su estructura (servicios inyectables, una carpeta por funcionalidad) es parecida a la del backend en Spring, lo que facilita mantener los dos lados con las mismas reglas.
- Curva de aprendizaje mayor que la de React o Vue, y más código repetitivo en componentes sencillos.
- Las versiones mayores salen cada seis meses: cada subida es una tarea con su issue (ver las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md)).
