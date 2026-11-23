# Los Chicharros V.C.

Web del club de voleibol ficticio Los Chicharros V.C.: proyecto fullstack de portfolio con
Spring Boot 4.2 (Java 25), Angular 22 con SSR y MySQL.

La documentación del proyecto está en [docs/](docs/). Cómo arrancar el backend:
[backend/README.md](backend/README.md); el frontend: [frontend/README.md](frontend/README.md).

## Pre-commit

Antes de cada commit, [Husky](https://typicode.github.io/husky/) y
[lint-staged](https://github.com/lint-staged/lint-staged) formatean y revisan solo los ficheros
del commit:

| Ficheros | Herramienta |
| --- | --- |
| `frontend/**/*.ts`, `*.html` | ESLint con `--fix` |
| todo `frontend/` | Prettier |
| `backend/src/**/*.java` | Spotless (google-java-format) |

Si una herramienta corrige algo, el cambio entra en el mismo commit; si ESLint encuentra un error
que no sabe arreglar, el commit se cancela y muestra el error. La CI vuelve a comprobarlo todo.

Se activa una vez por clon, en la raíz del repositorio (necesita Node 24 y pnpm):

```sh
pnpm install
```

La configuración está en `lint-staged.config.mjs` y `.husky/pre-commit`;
`scripts/pre-commit.mjs` lanza cada herramienta desde su carpeta y funciona igual en Windows.
