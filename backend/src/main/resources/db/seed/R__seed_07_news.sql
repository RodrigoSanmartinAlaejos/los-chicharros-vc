-- Semilla de dev y staging: las noticias de news_articles.csv (docs/04-datos/datos-ejemplo/).
-- No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.
-- Es repetible e idempotente: INSERT IGNORE sobre la clave única del slug.
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Horarios de entrenamiento de la temporada 2026-27', 'horarios-de-entrenamiento-de-la-temporada-2026-27', 'Días, horas y pabellón de entrenamiento de los diez equipos del club.', 'Ya están los horarios de entrenamiento de la temporada 2026-27. Empiezan el lunes 14 de septiembre.

| Equipos | Días | Hora | Pabellón |
| --- | --- | --- | --- |
| Alevín e infantil | Lunes y miércoles | 17:00 a 18:30 | Polideportivo La Marina |
| Cadete | Martes y jueves | 17:30 a 19:00 | Polideportivo La Marina |
| Juvenil | Lunes, miércoles y viernes | 19:00 a 20:30 | Pabellón del Puerto |
| Sénior | Martes, jueves y viernes | 20:30 a 22:00 | Pabellón del Puerto |

Cualquier cambio se publicará en esta web.', '/assets/imagenes/noticia-comunicado.svg', 'STATEMENT', FALSE, '2026-09-07 09:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Presentación de los equipos de la temporada 2026-27', 'presentacion-de-los-equipos-de-la-temporada-2026-27', 'El club presenta a sus diez equipos, de alevín a sénior, en el Pabellón del Puerto.', 'El sábado 26 de septiembre presentamos a los diez equipos de Los Chicharros V.C. en el Pabellón del Puerto.

Esta temporada el club tiene equipo masculino y femenino en todas las categorías: alevín, infantil, cadete, juvenil y sénior.
Las ligas empiezan el fin de semana del 3 de octubre.

¡Os esperamos en la grada!', '/assets/imagenes/portada.svg', 'EVENT', TRUE, '2026-09-21 17:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Victoria del cadete masculino ante Club Voleibol Tabaiba (3-0)', 'victoria-del-cadete-masculino-ante-club-voleibol-tabaiba-3-0', 'Crónica de la jornada 3 de la Liga Insular Cadete masculina, jugada en casa.', 'El cadete masculino ganó en casa por 3-0 en la jornada 3.

El equipo controló el partido desde el saque y suma los tres puntos.

El próximo partido es en la jornada 4, contra Roque Vóley.', '/assets/imagenes/noticia-cronica.svg', 'MATCH_REPORT', FALSE, '2026-11-02 12:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Victoria del juvenil femenino ante Vóley Barlovento (3-2)', 'victoria-del-juvenil-femenino-ante-voley-barlovento-3-2', 'Crónica de la jornada 4 de la Liga Insular Juvenil femenina, jugada a domicilio.', 'El juvenil femenino ganó a domicilio por 3-2 en la jornada 4.

Fue un partido muy igualado que se decidió en el quinto set; dos puntos que valen mucho.

El próximo partido es en la jornada 5, contra Club Voleibol Alisios.', '/assets/imagenes/noticia-cronica.svg', 'MATCH_REPORT', FALSE, '2026-11-16 12:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Derrota del sénior masculino ante Club Voleibol Malpaís (1-3)', 'derrota-del-senior-masculino-ante-club-voleibol-malpais-1-3', 'Crónica de la jornada 5 de la Liga Insular Sénior masculina, jugada en casa.', 'El sénior masculino perdió en casa por 1-3 en la jornada 5.

El rival fue superior esta vez; toca seguir trabajando en los entrenamientos.

El próximo partido es en la jornada 6, contra Vóley Barlovento.', '/assets/imagenes/noticia-cronica.svg', 'MATCH_REPORT', FALSE, '2026-11-30 12:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Campus de Navidad de voleibol', 'campus-de-navidad-de-voleibol', 'Del 28 al 30 de diciembre, campus para nacidos entre 2011 y 2018, socios o no del club.', 'Del 28 al 30 de diciembre organizamos el Campus de Navidad en el Polideportivo La Marina, de 9:30 a 13:30.

- Para nacidos entre 2011 y 2018, sean o no del club.
- Plazas limitadas: 40.
- Inscripción en la secretaría del club hasta el 18 de diciembre.', '/assets/imagenes/noticia-evento.svg', 'EVENT', TRUE, '2026-12-01 09:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Victoria del sénior femenino ante Vóley Barlovento (3-1)', 'victoria-del-senior-femenino-ante-voley-barlovento-3-1', 'Crónica de la jornada 6 de la Liga Insular Sénior femenina, jugada a domicilio.', 'El sénior femenino ganó a domicilio por 3-1 en la jornada 6.

El equipo controló el partido desde el saque y suma los tres puntos.

El próximo partido es en la jornada 7, contra Club Voleibol Alisios.', '/assets/imagenes/noticia-cronica.svg', 'MATCH_REPORT', FALSE, '2026-12-13 12:00:00', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
INSERT IGNORE INTO news_articles (title, slug, summary, content, image_url, type, featured, published_at, created_at, updated_at)
  VALUES ('Asamblea de socios de enero', 'asamblea-de-socios-de-enero', 'Borrador: convocatoria de la asamblea ordinaria de socios.', 'Texto pendiente de aprobar por la junta directiva.', NULL, 'STATEMENT', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
