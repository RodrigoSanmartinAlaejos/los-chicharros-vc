# Paleta de colores: Atlántico y arena

Última revisión: 2026-11-12 (decidida el 2026-11-10; colores de estado y contraste revisados el 2026-11-12).

Paleta elegida para la web del club (tema claro).

## Colores

| Rol | Nombre | HEX | Variable | Uso |
|---|---|---|---|---|
| Fondo | Arena | `#F5F1E8` | `--color-background` | Fondo general de página |
| Principal | Azul petróleo | `#12394A` | `--color-primary` | Cabecera, etiquetas, tarjetas destacadas, enlaces |
| Acento | Latón | `#C9A66B` | `--color-accent` | Botones, subrayado del menú activo sobre la cabecera, líneas y detalles |
| Texto principal | Azul tinta | `#0F2A36` | `--color-text` | Títulos y cuerpo de texto |
| Texto secundario | Gris azulado | `#4A5B63` | `--color-text-secondary` | Fechas, resúmenes, textos de apoyo |
| Superficie | Blanco cálido | `#FFFDF8` | `--color-surface` | Fondo de tarjetas |
| Borde | Arena oscura | `#DDD5C5` | `--color-border` | Bordes decorativos de tarjetas y separadores |
| Borde de campo | Arena tostada | `#8A7F6A` | `--color-border-strong` | Bordes de inputs, selects y checkboxes |
| Relleno neutro | Arena media | `#E6DFD0` | `--color-muted` | Huecos de imagen y fondos secundarios |
| Enlace hover | Petróleo oscuro | `#0A2531` | `--color-primary-hover` | Estado hover de enlaces |
| Foco | Azul petróleo | `#12394A` | `--color-focus` | Contorno de 2 px en cualquier elemento con foco |
| Error | Rojo teja | `#B42318` | `--color-error` | Mensajes de error y bordes de campos con error |
| Éxito | Verde pino | `#1E6B3A` | `--color-success` | Confirmaciones |
| Aviso | Ámbar oscuro | `#9A5B00` | `--color-warning` | Avisos no bloqueantes y franja de `staging` |

## Contraste comprobado (WCAG 2.1)

Calculado con la fórmula de luminancia relativa de WCAG el 2026-11-12. Objetivo AA: 4,5:1 en texto normal y 3:1 en elementos de interfaz (bordes de campos, indicadores de estado, foco).

| Combinación | Contraste | Mínimo | Resultado |
|---|---|---|---|
| Azul tinta sobre arena | 13,3:1 | 4,5:1 | Cumple |
| Arena sobre azul petróleo | 10,9:1 | 4,5:1 | Cumple |
| Gris azulado sobre arena | 6,3:1 | 4,5:1 | Cumple |
| Azul tinta sobre latón (botones) | 6,5:1 | 4,5:1 | Cumple |
| Latón sobre azul petróleo | 5,4:1 | 3:1 | Cumple |
| Borde de campo sobre blanco cálido | 3,9:1 | 3:1 | Cumple |
| Borde de campo sobre arena | 3,5:1 | 3:1 | Cumple |
| Error sobre arena | 5,8:1 | 4,5:1 | Cumple |
| Éxito sobre arena | 5,8:1 | 4,5:1 | Cumple |
| Aviso sobre arena | 4,8:1 | 4,5:1 | Cumple |
| Foco (azul petróleo) sobre arena | 10,9:1 | 3:1 | Cumple |
| Borde decorativo sobre blanco cálido | 1,4:1 | — | Solo decorativo: nunca para campos |
| Latón sobre arena | 2,0:1 | 3:1 | No cumple: nunca como texto ni indicador sobre fondo claro |

## Reglas de contraste

- El latón `#C9A66B` no se usa como texto, enlace ni indicador de estado sobre arena o blanco.
- El latón sí vale como fondo con texto azul tinta `#0F2A36` encima (botones), y como texto o subrayado sobre azul petróleo.
- Si el menú activo va sobre fondo claro, su subrayado es azul petróleo, no latón.
- Sobre azul petróleo, el texto va en arena `#F5F1E8`.
- Sobre arena o blanco cálido, el texto va en azul tinta (principal) o gris azulado (secundario), y los enlaces en azul petróleo.
- El borde `#DDD5C5` es solo decorativo; los campos de formulario usan `--color-border-strong`.
- Los elementos deshabilitados usan su color normal con opacidad del 50 % (WCAG no exige contraste en elementos deshabilitados).
- Los errores no se indican solo con color: van acompañados de texto junto al campo.

## Variables CSS

Nombres en inglés, según las [reglas técnicas](../07-desarrollo/reglas-tecnicas.md) del proyecto.

```css
:root {
  --color-background: #F5F1E8;
  --color-primary: #12394A;
  --color-primary-hover: #0A2531;
  --color-accent: #C9A66B;
  --color-text: #0F2A36;
  --color-text-secondary: #4A5B63;
  --color-surface: #FFFDF8;
  --color-border: #DDD5C5;
  --color-border-strong: #8A7F6A;
  --color-muted: #E6DFD0;
  --color-focus: #12394A;
  --color-error: #B42318;
  --color-success: #1E6B3A;
  --color-warning: #9A5B00;
}
```

## Tipografía usada en la vista previa

- Títulos: Space Grotesk (600 y 700)
- Texto: Inter (400, 500 y 600)

## Referencia visual

Vista previa de cabecera y tarjetas: https://claude.ai/artifact/2M5Yjxf3zDttYUf4gCaHrw (primer tablero, "1. Atlántico y arena"). La vista previa no incluye todavía los colores de estado ni el borde de campo.
