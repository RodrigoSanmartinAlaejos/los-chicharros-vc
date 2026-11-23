# Frontend

Última revisión: 2026-11-23.

Web pública y áreas privadas de Los Chicharros V.C.: Angular 22 con renderizado en servidor (SSR).
Las convenciones están en las [reglas técnicas](../docs/07-desarrollo/reglas-tecnicas.md).

Requisitos: Node 24 (`.nvmrc`) y pnpm. Nunca se usa `npm`, `npx` ni el `ng` global.

| Tarea                                            | Comando                               |
| ------------------------------------------------ | ------------------------------------- |
| Instalar dependencias                            | `pnpm install`                        |
| Servidor de desarrollo (`http://localhost:4200`) | `pnpm start`                          |
| Generar un componente                            | `pnpm ng generate component <nombre>` |
| Compilar                                         | `pnpm build`                          |
| Tests unitarios (Vitest)                         | `pnpm test`                           |
| Servir la compilación con SSR                    | `pnpm serve:ssr:frontend`             |

## Configuración

Un único build para todos los entornos: el servidor SSR lee estas variables al arrancar. En local
salen del `.env` de la raíz del repositorio (ver `.env.example`); en Render, del panel.

| Variable           | Para qué                                                                                       | Local                   |
| ------------------ | ---------------------------------------------------------------------------------------------- | ----------------------- |
| `API_URL`          | URL de la API: a ella se reenvía `/api/**` y la llaman las páginas renderizadas en el servidor | `http://localhost:8080` |
| `APP_ENV`          | `dev`, `staging` o `prod`; fuera de `prod` la web lleva `noindex`                              | `dev`                   |
| `NG_ALLOWED_HOSTS` | Dominios que atiende el servidor; Angular rechaza el resto con un 400 (protección SSRF)        | `localhost`             |
| `TRUST_PROXY`      | Proxies delante del servidor de los que se acepta `X-Forwarded-For` (`1` en Render)            | vacío                   |

Con `pnpm start`, el servidor de desarrollo reenvía `/api` a la API con `proxy.conf.json`; con el
servidor compilado lo hace `src/server/api-proxy.ts` (ADR-004), con el mismo límite de 6 MB por
petición que la API.
