# ADR-004: El servidor SSR hace de proxy de la API

Última revisión: 2026-11-22.

- **Estado:** Aceptado
- **Fecha:** 2026-11-12

## Contexto

El frontend y la API se despliegan en Render, en dos subdominios de `onrender.com`. Ese dominio está en la Public Suffix List, así que dos subdominios suyos cuentan como sitios distintos. El refresh token viaja en una cookie `HttpOnly` con `SameSite=Strict`: si el navegador llamara directamente a la API, esa cookie sería de terceros y no se enviaría. Además, el plan gratuito de Render no ofrece red privada entre servicios.

## Decisión

El navegador solo habla con el frontend. El servidor Node del SSR reenvía `/api/**` a la API, con `X-Forwarded-For` y el mismo tamaño máximo de cuerpo que la API. La cookie de refresco no lleva atributo `Domain` y CORS está desactivado en la API. En local hace lo mismo el `proxy.conf.json` del servidor de desarrollo.

## Alternativas descartadas

- **Dominio propio con dos subdominios:** resuelve el problema, pero cuesta dinero y el presupuesto es 0 €.
- **CORS con cookie `SameSite=None`:** depende de cookies de terceros, que los navegadores bloquean cada vez más.
- **Token en `localStorage`:** queda expuesto a cualquier XSS.

## Consecuencias

- La cookie de refresco es de primera parte y no hace falta CORS.
- Cada petición da un salto más (navegador → frontend → API); el SSR llama a la URL pública de la API (`API_URL`).
- La API solo se fía de `X-Forwarded-For` cuando viene del proxy (`server.forward-headers-strategy`), para que el bloqueo de login y los logs usen la IP real.
- Si el frontend está dormido, la API tampoco recibe tráfico: al arrancar, el SSR lanza una petición a `/actuator/health` para despertar a los dos.
