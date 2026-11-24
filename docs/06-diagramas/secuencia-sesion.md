# Secuencia: renovación y cierre de sesión

Última revisión: 2026-11-23.

Cómo se mantiene viva la sesión mientras el administrador trabaja, qué pasa si alguien reutiliza un refresh token robado y cómo se cierra (historia H10).

```mermaid
sequenceDiagram
    autonumber
    participant A as Administrador
    participant N as Navegador (Angular)
    participant API as API (a través del proxy)
    participant DB as Base de datos
    Note over N: Al entrar en el panel sin access token, un minuto antes de que caduque (si ha habido actividad) o tras un 401
    N->>API: POST /api/v1/auth/refresh (cookie refresh_token)
    API->>DB: Busca el token por su hash SHA-256
    alt No existe, caducado o sesión de más de 7 días
        API-->>N: 401
        N-->>A: Lleva a /login
    else Ya estaba revocado (reutilización)
        API->>DB: Revoca todos los refresh tokens del usuario
        API-->>N: 401
        N-->>A: Lleva a /login
    else Válido
        API->>DB: Marca el actual como revocado y guarda uno nuevo (2 h más, misma fecha de fin de sesión)
        API-->>N: 200 {accessToken} y Set-Cookie con el token nuevo
    end
    A->>N: Pulsa «Cerrar sesión»
    N->>API: POST /api/v1/auth/logout (cookie)
    API->>DB: Revoca el refresh token
    API-->>N: 204 y cookie borrada
    N->>N: Borra el access token de memoria
    N-->>A: Lleva a la portada
```

- La sesión es deslizante: cada renovación alarga 2 horas, pero nunca más allá de 7 días desde el login.
- Sin actividad, el frontend no renueva: el access token caduca y, si pasan 2 horas, también el refresh token, así que hay que volver a entrar. Si la API responde 401 a una petición, el frontend renueva una vez y la repite.
- Cualquier 401 de `/refresh` borra la cookie (`Max-Age=0`).
- Si dos pestañas renuevan a la vez, la segunda usa un token ya rotado y cierra la sesión; el frontend centraliza la renovación en un único servicio para evitarlo.
