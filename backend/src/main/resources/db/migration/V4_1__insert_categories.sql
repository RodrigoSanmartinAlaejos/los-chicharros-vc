-- Catálogo de categorías: va por migración también en producción (reglas técnicas, «Bases de
-- datos»). Son las de la ficha del club; el administrador puede cambiarlas desde la configuración.
INSERT INTO categories (name, gender, sort_order, created_at, updated_at) VALUES
  ('Alevín', 'MALE', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Alevín', 'FEMALE', 2, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Infantil', 'MALE', 3, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Infantil', 'FEMALE', 4, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Cadete', 'MALE', 5, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Cadete', 'FEMALE', 6, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Juvenil', 'MALE', 7, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Juvenil', 'FEMALE', 8, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Sénior', 'MALE', 9, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Sénior', 'FEMALE', 10, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
