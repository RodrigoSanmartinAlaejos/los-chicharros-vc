# Secuencia: publicar una noticia

Última revisión: 2026-11-23.

Del borrador a la noticia publicada y compartida en redes (historias H12, H13, H14 y H03).

```mermaid
sequenceDiagram
    autonumber
    participant A as Administrador
    participant N as Navegador (panel)
    participant API as API
    participant IMG as ImageStorage (Cloudinary)
    participant DB as Base de datos
    participant V as Visitante o red social
    participant P as Frontend SSR
    A->>N: Escribe título, resumen, tipo y cuerpo en Markdown
    N->>N: Vista previa y copia del borrador en el navegador
    N->>API: POST /api/v1/news
    API->>API: Valida (Bean Validation) y genera el slug
    API->>DB: Guarda sin published_at (borrador)
    API-->>N: 201 con Location
    A->>N: Elige la imagen
    N->>API: PUT /api/v1/news/{id}/image (multipart)
    API->>API: Comprueba tipo y tamaño (5 MB)
    API->>IMG: Sube la imagen
    IMG-->>API: URL
    API->>DB: Guarda image_url
    API-->>N: 200
    A->>N: Pulsa «Publicar»
    N->>API: PATCH /api/v1/news/{id} {publishedAt}
    API->>DB: Guarda published_at
    API-->>N: 200
    V->>P: GET /noticias/{slug}
    P->>API: GET /api/v1/news/by-slug/{slug}
    API->>DB: Busca la noticia publicada
    API->>API: Markdown a HTML y saneado con lista blanca
    API-->>P: 200 con contentHtml
    P->>P: Renderiza la página con título, descripción y Open Graph
    P-->>V: HTML completo (con vista previa al compartir)
```

- El frontend nunca sanea HTML por su cuenta: inserta el que devuelve la API.
- Una noticia retirada vuelve a borrador con `PATCH {publishedAt: null}` y la web pública responde 404.
