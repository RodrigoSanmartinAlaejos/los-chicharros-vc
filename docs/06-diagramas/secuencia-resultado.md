# Secuencia: introducir un resultado

Última revisión: 2026-11-23.

Cómo registra el administrador el resultado de un partido y actualiza la clasificación en la fase 1 (historias H20 y H21). En la fase 3 el resultado será set a set y la clasificación se recalculará sola.

```mermaid
sequenceDiagram
    autonumber
    participant A as Administrador
    participant N as Navegador (panel)
    participant API as API
    participant DB as Base de datos
    participant V as Visitante
    A->>N: Abre el partido jugado e introduce 3-1
    N->>API: PATCH /api/v1/matches/{id} {status: FINISHED, homeSets: 3, awaySets: 1}
    API->>API: Uno llega a 3 sets y el otro a menos de 3
    alt Marcador imposible
        API-->>N: 400 con el error en el campo
    else Válido
        API->>DB: Guarda estado y sets (y created_by / updated_by)
        API-->>N: 200
    end
    A->>N: Abre la clasificación de la competición y corrige la tabla
    N->>API: PUT /api/v1/competitions/{id}/standings
    API->>API: Valida que no haya números negativos salvo los puntos
    API->>DB: Sustituye las filas de la competición
    API-->>N: 200 con la tabla ordenada
    V->>API: GET /api/v1/competitions/{id}/standings (a través del SSR)
    API-->>V: Tabla por puntos, ganados y cociente de sets
```

- En la fase 1 la clasificación se edita a mano; el resultado y la tabla son dos pasos separados.
- Desde la fase 3, guardar el resultado recalcula la tabla en el servicio de clasificación y deja rastro en el historial de cambios.
