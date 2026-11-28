# Imágenes de ejemplo

Última revisión: 2026-11-28.

Imágenes para los datos de ejemplo de la web: portada, equipos, personas sin foto y noticias. Todas son **ilustraciones propias**, dibujadas en SVG con los colores de la [paleta](../../02-requisitos/paleta-colores.md). No hay fotos de personas reales, así que no hace falta consentimiento de imagen ni hay riesgo de mostrar a menores.

## Licencia

Obra propia del proyecto, con la misma licencia que el repositorio: [MIT](../../../LICENSE), © Rodrigo Sanmartín Alaejos. Se pueden usar, cambiar y redistribuir sin pedir permiso.

| Fichero | Tamaño | Uso | Licencia |
| --- | --- | --- | --- |
| [`portada.svg`](portada.svg) | 1600 × 900 | Cabecera de la portada y noticia de presentación de equipos | Propia, MIT |
| [`equipo.svg`](equipo.svg) | 800 × 600 | Foto de equipo cuando no hay foto (`teams.crest_url` de los equipos del club) | Propia, MIT |
| [`persona.svg`](persona.svg) | 400 × 400 | Silueta para personas sin foto o sin consentimiento de imagen | Propia, MIT |
| [`noticia-cronica.svg`](noticia-cronica.svg) | 1200 × 675 | Imagen de las crónicas de partido (`MATCH_REPORT`) | Propia, MIT |
| [`noticia-evento.svg`](noticia-evento.svg) | 1200 × 675 | Imagen de los eventos (`EVENT`) | Propia, MIT |
| [`noticia-comunicado.svg`](noticia-comunicado.svg) | 1200 × 675 | Imagen de los comunicados (`STATEMENT`) | Propia, MIT |
| [`../escudo.svg`](../escudo.svg) | 200 × 240 | Escudo del club | Propia, MIT |

Cada SVG lleva un `<title>` que describe la imagen, para lectores de pantalla.

La web las sirve en `/assets/imagenes/` desde una copia en `frontend/public/assets/imagenes/` (desde el #58): Angular no puede leer ficheros de fuera de `frontend/`. Si cambias una, cópiala también allí.

## Si se añaden fotos

Las ilustraciones bastan para la demo. Para añadir fotos reales:

- **Licencia que permita uso comercial y modificación sin pedir permiso:** CC0 o dominio público (por ejemplo, el [directorio de fotos de WordPress](https://wordpress.org/photos/) o Wikimedia Commons). Las licencias CC BY y CC BY-SA también valen, pero obligan a citar al autor en la web.
- **Sin menores reconocibles.** Mejor fotos de balón, red, pista o grada que de caras.
- **Se anota cada foto en la tabla de arriba:** URL de origen, autor, licencia y fecha en que se comprobó.
- **Peso:** se reduce a 1600 px de ancho como máximo y se convierte a WebP antes de subirla (RNF de rendimiento).

Candidata revisada el 2026-11-23, sin añadir todavía al repositorio: [balón sobre arena de playa](https://wordpress.org/photos/photo/559688c942/), de Nilo Velez, CC0 según su página. El original mide 6000 × 4000 y pesa 6 MB, así que habría que reducirla antes.
