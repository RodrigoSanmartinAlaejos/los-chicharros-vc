# Frontend

Última revisión: 2026-11-16.

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
