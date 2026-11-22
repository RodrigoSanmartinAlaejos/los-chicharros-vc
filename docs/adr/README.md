# Registro de decisiones de arquitectura (ADR)

Última revisión: 2026-11-22.

Cada decisión técnica importante se escribe en un ADR corto (menos de una página) con la plantilla de Michael Nygard. Un ADR aceptado no se edita: si la decisión cambia, se escribe otro que lo sustituye y el antiguo pasa a «Sustituido por ADR-NNN».

| ADR | Decisión | Estado | Fecha |
| --- | --- | --- | --- |
| [001](001-angular-frontend.md) | Frontend con Angular | Aceptado | 2026-11-03 |
| [002](002-angular-ssr.md) | Angular con renderizado en servidor (SSR) | Aceptado | 2026-11-10 |
| [003](003-person-user-split.md) | `Person` separada de `User`; la cuenta es opcional | Aceptado | 2026-11-10 |
| [004](004-ssr-api-proxy.md) | El servidor SSR hace de proxy de la API | Aceptado | 2026-11-12 |

## Plantilla

```markdown
# ADR-NNN: Título en forma de decisión

Última revisión: AAAA-MM-DD.

- **Estado:** Propuesto | Aceptado | Sustituido por ADR-NNN
- **Fecha:** AAAA-MM-DD

## Contexto

Qué problema hay y qué condiciona la decisión.

## Decisión

Qué se decide, en una o dos frases.

## Alternativas descartadas

- **Alternativa:** por qué no.

## Consecuencias

Qué se gana, qué se pierde y qué obliga a hacer.
```
