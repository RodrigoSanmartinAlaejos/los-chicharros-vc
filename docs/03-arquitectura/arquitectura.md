# Arquitectura

Última revisión: 2026-11-22.

Cómo está montada la web: con quién se relaciona, de qué piezas se compone y cómo se organiza el backend por dentro. Es el entregable del paso 8 de la planificación, con diagramas C4 de contexto y de contenedores. Las decisiones de fondo están en los [ADR](../adr/README.md) y las reglas de cada pieza en las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md).

## Contexto

Quién usa la web y con qué servicios externos habla. Entre paréntesis, la fase en la que entra cada uno.

```mermaid
flowchart LR
    subgraph Personas
        V(["Visitante"])
        F(["Socios y tutores (F2)"])
        D(["Entrenadores y coordinador (F2)"])
        G(["Comunicación, tesorero y administrador"])
    end
    W["Web de Los Chicharros V.C."]
    subgraph Servicios externos
        CL["Cloudinary<br/>imágenes"]
        BR["Brevo<br/>emails (F2)"]
        OSM["OpenStreetMap y Nominatim<br/>mapas (F4)"]
        ST["Stripe<br/>pagos (F4)"]
        SE["Sentry<br/>errores (F5)"]
        MO["Monitor externo<br/>caídas"]
    end
    V & F & D & G -->|HTTPS| W
    W --> CL & BR & OSM & ST & SE
    MO -->|comprueba cada hora| W
```

## Contenedores

Las piezas que se despliegan y cómo se comunican. El navegador solo habla con el frontend, que reenvía `/api` a la API ([ADR-004](../adr/004-ssr-api-proxy.md)).

```mermaid
flowchart LR
    B(["Navegador"])
    subgraph Render
        FE["Frontend<br/>Angular 22 con SSR<br/>proceso Node"]
        API["API<br/>Spring Boot 4.1, Java 25<br/>imagen Docker"]
    end
    DB[("TiDB Cloud Starter<br/>club_prod · club_staging<br/>(MySQL 8.4 en local)")]
    subgraph GitHub
        GA["GitHub Actions<br/>CI, migraciones y despliegue"]
    end
    EXT["Cloudinary · Brevo · Stripe · Sentry"]
    B -->|"HTTPS: páginas y /api/**"| FE
    FE -->|"/api/** reenviado (proxy)<br/>y peticiones del SSR a API_URL"| API
    API -->|"JDBC (JPA)"| DB
    API --> EXT
    EXT -->|"webhook de pagos (F4)"| API
    GA -->|"Flyway con *_migrator"| DB
    GA -->|"deploy hook"| FE & API
```

| Contenedor | Tecnología | Responsabilidad |
| --- | --- | --- |
| Frontend | Angular 22 con SSR en Node 24, en Render | Renderizar las páginas públicas en el servidor, servir la aplicación y hacer de proxy de `/api` |
| API | Spring Boot 4.1 con Java 25, imagen Docker en Render | Lógica de negocio, seguridad, validación y acceso a datos |
| Base de datos | TiDB Cloud Starter (`staging` y `prod`); MySQL 8.4 en local y en los tests | Guardar los datos; el esquema solo cambia con Flyway |
| GitHub Actions | Workflows del repositorio | CI en cada PR, migraciones y despliegue con aprobación manual |

## Capas del backend

Un paquete por funcionalidad (`news`, `matches`, `teams`…) con estas capas dentro. La lógica de negocio vive siempre en los servicios.

```mermaid
flowchart TB
    REQ(["Petición HTTP"])
    subgraph Seguridad
        SEC["Filtros de Spring Security<br/>JWT, límite de intentos (Bucket4j)"]
    end
    subgraph Funcionalidad
        C["Controller<br/>recibe, valida con @Valid y delega"]
        S["Service<br/>reglas de negocio, @Transactional,<br/>@PreAuthorize y comprobación de propiedad"]
        M["Mapper (MapStruct)<br/>entidad ↔ DTO"]
        R["Repository<br/>Spring Data JPA"]
        E["Entidades JPA"]
    end
    subgraph Común
        ERR["@RestControllerAdvice<br/>errores RFC 7807"]
        AUD["Auditoría de Spring Data<br/>created_by, updated_by"]
        IMG["ImageStorage<br/>disco en dev · Cloudinary"]
        MAIL["EmailSender (F2)<br/>Mailpit en dev · Brevo"]
    end
    DB[("Base de datos")]
    REQ --> SEC --> C
    C -->|"DTO (record)"| S
    S --> M
    S --> R --> E --> DB
    S --> IMG & MAIL
    C -. excepción .-> ERR
    E -.-> AUD
```

Reglas que se ven en el diagrama:

- Las entidades JPA nunca salen del servicio: al controlador le llegan DTOs (`record`).
- El controlador no tiene lógica ni toca repositorios.
- Los servicios externos se usan siempre a través de una interfaz (`ImageStorage`, `EmailSender`) cuya implementación elige el perfil de Spring.
- Los errores se convierten en `ProblemDetail` en un único sitio.

## Estructura del frontend

```mermaid
flowchart TB
    subgraph app
        CORE["core/<br/>interceptores (JWT, errores), guards,<br/>servicios únicos, layout"]
        SHARED["shared/<br/>componentes, pipes y directivas reutilizables"]
        subgraph features
            PUB["news/, teams/, matches/, standings/, club/<br/>públicas: SSR"]
            ADM["admin/ (/gestion)<br/>privada: solo navegador"]
        end
    end
    PUB & ADM --> SHARED
    PUB & ADM --> CORE
    PUB & ADM -->|"/api/v1"| P(["Proxy del SSR"])
```

Cada funcionalidad tiene sus páginas, sus componentes, su `*.service.ts` (el único que llama a la API, a través de los interceptores de `core/`) y su `*.models.ts`, y se carga de forma diferida. Qué rutas se renderizan en el servidor lo decide `app.routes.server.ts`.
