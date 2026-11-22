# ADR-002: Angular con renderizado en servidor (SSR)

Última revisión: 2026-11-22.

- **Estado:** Aceptado
- **Fecha:** 2026-11-10

## Contexto

La parte pública (noticias, equipos, calendario, clasificación) tiene que aparecer en buscadores, y al compartir una noticia en redes sociales tiene que verse la vista previa con título, resumen e imagen. Los rastreadores y las redes leen el HTML que devuelve el servidor: con una SPA pura reciben una página vacía.

## Decisión

El frontend usa Angular con SSR desde el inicio. Las páginas públicas se renderizan en el servidor y las áreas privadas y el panel de gestión solo en el navegador (`app.routes.server.ts`).

## Alternativas descartadas

- **SPA pura:** sin SEO ni vista previa en redes.
- **Prerenderizado estático:** las noticias y los resultados cambian a diario; habría que recompilar y desplegar con cada cambio.
- **Añadir SSR más adelante:** obliga a revisar todo el código que use `window`, `document` o `localStorage`; sale más barato hacerlo bien desde el primer componente.

## Consecuencias

- El frontend se despliega como un proceso Node, no como ficheros estáticos, y consume horas del plan gratuito de Render.
- El código no puede usar `window`, `document`, `localStorage` ni `navigator` directamente: va en `afterNextRender` o tras `isPlatformBrowser`.
- El servidor solo llama a endpoints públicos; nunca hace peticiones autenticadas.
- Cada página pública pone su título y su descripción, y el detalle de noticia sus etiquetas Open Graph.
- Un único build para todos los entornos: el servidor lee `API_URL` y `APP_ENV` al arrancar.
