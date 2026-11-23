# Secuencia: login del administrador

Última revisión: 2026-11-23.

Cómo entra el administrador en el panel y cómo accede después a una ruta protegida (historias H09 y H11). Todas las peticiones del navegador pasan por el proxy del SSR ([ADR-004](../adr/004-ssr-api-proxy.md)).

```mermaid
sequenceDiagram
    autonumber
    participant A as Administrador
    participant N as Navegador (Angular)
    participant P as Frontend SSR (proxy)
    participant API as API (Spring Boot)
    participant DB as Base de datos
    A->>N: Escribe email y contraseña y pulsa «Entrar»
    N->>P: POST /api/v1/auth/login
    P->>API: Reenvía con X-Forwarded-For
    API->>API: Límite de intentos (email + IP y 20 por minuto)
    alt Bloqueado o demasiadas peticiones
        API-->>N: 401 genérico o 429
    else Permitido
        API->>DB: Busca el usuario por email
        API->>API: Compara la contraseña con BCrypt
        alt Credenciales incorrectas
            API->>API: Suma un fallo para email + IP
            API-->>N: 401 genérico (mismo mensaje exista o no el email)
        else Correctas
            API->>API: Pone a cero los fallos
            API->>DB: Guarda el hash del refresh token (2 h, sesión de 7 días)
            API-->>N: 200 {accessToken, expiresIn: 900} y Set-Cookie refresh_token
            N->>N: Guarda el access token solo en memoria
            N-->>A: Abre /gestion
        end
    end
    A->>N: Abre una sección del panel
    N->>P: GET /api/v1/news?status=draft con Authorization: Bearer
    P->>API: Reenvía
    API->>API: Valida la firma y la caducidad del JWT y el rol
    API->>DB: Consulta
    API-->>N: 200 con los datos
```

- El access token nunca se guarda en `localStorage` ni `sessionStorage`.
- La cookie lleva `HttpOnly`, `Secure`, `SameSite=Strict` y `Path=/api/v1/auth`, así que solo viaja a las rutas de autenticación.
