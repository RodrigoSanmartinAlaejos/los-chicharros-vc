-- Semilla de dev y staging: la temporada 2026-27 activa y los dos pabellones de la ficha del club
-- (docs/04-datos/datos-ejemplo/). Es repetible e idempotente: se vuelve a aplicar si cambia el
-- fichero y no duplica filas (INSERT IGNORE sobre las claves únicas).
INSERT IGNORE INTO seasons (name, start_date, end_date, active, created_at, updated_at) VALUES
  ('2026-27', '2026-09-01', '2027-06-30', TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));

INSERT IGNORE INTO venues (name, address, latitude, longitude, created_at, updated_at) VALUES
  ('Pabellón del Puerto', 'Avenida Marítima, 12, Santa Cruz de Tenerife', 28.468200, -16.246600,
    UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
  ('Polideportivo La Marina', 'Calle La Marina, 3, Santa Cruz de Tenerife', 28.471500, -16.250300,
    UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
