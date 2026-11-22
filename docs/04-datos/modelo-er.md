# Modelo de datos: diagrama entidad-relación

Última revisión: 2026-11-22.

Modelo de todo el proyecto a alto nivel. Es el entregable del paso 7 de la planificación, junto con el [diccionario de datos de la fase 1](diccionario-fase-1.md); el de cada fase siguiente se escribe al planificarla. Los nombres son los de la base de datos (en inglés y en plural, según las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md)); su equivalente en español está en el [glosario](../01-vision/glosario.md).

## Fase 1

Lo necesario para la web pública y el panel del administrador.

```mermaid
erDiagram
    seasons ||--o{ teams : "tiene"
    seasons ||--o{ competitions : "tiene"
    categories ||--o{ teams : "agrupa"
    categories ||--o{ competitions : "agrupa"
    teams ||--o{ team_memberships : "plantilla"
    people ||--o{ team_memberships : "juega o entrena"
    competitions ||--o{ matchdays : "se divide en"
    competitions ||--o{ standings : "clasificación"
    teams ||--o{ standings : "aparece en"
    matchdays ||--o{ matches : "incluye"
    teams ||--o{ matches : "local"
    teams ||--o{ matches : "visitante"
    venues |o--o{ matches : "se juega en"
    users ||--o{ refresh_tokens : "sesiones"

    seasons { bigint id PK
        varchar name UK
        boolean active }
    categories { bigint id PK
        varchar name
        varchar gender }
    teams { bigint id PK
        bigint season_id FK
        bigint category_id FK
        boolean own_club }
    people { bigint id PK
        varchar first_name
        varchar last_name
        boolean image_consent }
    team_memberships { bigint id PK
        bigint team_id FK
        bigint person_id FK
        varchar type }
    competitions { bigint id PK
        bigint season_id FK
        bigint category_id FK }
    matchdays { bigint id PK
        bigint competition_id FK
        smallint number }
    matches { bigint id PK
        bigint matchday_id FK
        bigint home_team_id FK
        bigint away_team_id FK
        varchar status }
    venues { bigint id PK
        varchar name UK }
    standings { bigint id PK
        bigint competition_id FK
        bigint team_id FK
        smallint points }
    news_articles { bigint id PK
        varchar slug UK
        datetime published_at }
    users { bigint id PK
        varchar email UK }
    refresh_tokens { bigint id PK
        bigint user_id FK
        char token_hash UK }
```

`news_articles` no se relaciona con otras tablas. Las columnas `created_by` y `updated_by` de las tablas de negocio apuntan a `users`, pero no se dibujan para que el diagrama se pueda leer.

## Fases 2 a 4

Lo que se añade después, con las tablas de la fase 1 de las que depende.

```mermaid
erDiagram
    users |o--o| people : "cuenta de (F2)"
    users ||--o{ user_roles : "tiene (F2)"
    roles ||--o{ user_roles : "asignado (F2)"
    users ||--o{ account_tokens : "invitación o recuperación (F2)"
    people ||--o{ guardianships : "tutor (F2)"
    people ||--o{ guardianships : "menor (F2)"
    users ||--o{ announcements : "escribe (F2)"
    teams |o--o{ announcements : "destinatario (F2)"
    people |o--o{ announcements : "destinatario (F2)"
    matches ||--o{ match_sets : "sets (F3)"
    matches ||--o{ call_ups : "convoca (F3)"
    people ||--o{ call_ups : "convocado (F3)"
    teams ||--o{ training_sessions : "entrena (F3)"
    venues ||--o{ training_sessions : "en (F3)"
    training_sessions ||--o{ attendances : "pase de lista (F3)"
    people ||--o{ attendances : "asiste (F3)"
    users ||--o{ audit_log : "cambia (F3)"
    people ||--o{ membership_fees : "debe (F4)"
    seasons ||--o{ membership_fees : "de la temporada (F4)"
    membership_fees ||--o{ payments : "se paga con (F4)"
    users ||--o{ notifications : "recibe (F4)"
```

En la fase 3 la tabla `matches` gana `forfeited_team_id`, y `standings` deja de editarse a mano: la calcula el servicio de clasificación a partir de los partidos.

## Decisiones del modelo

Confirmadas el 2026-11-22.

- **`Person` y `User` separados** ([ADR-003](../adr/003-person-user-split.md)): la relación se añade en la fase 2 con la columna `users.person_id`, única y opcional.
- **Rivales en la misma tabla:** los equipos rivales son filas de `teams` con `own_club = false`, para que partidos y clasificaciones traten a todos igual.
- **Pabellón opcional en el partido:** solo se guardan los pabellones donde juega el club; un partido fuera de casa puede no tenerlo.
- **Una sola temporada activa:** lo garantiza el servicio, porque MySQL y TiDB no admiten un índice único sobre solo las filas con `active = true`.
