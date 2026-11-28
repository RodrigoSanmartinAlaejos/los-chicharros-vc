-- Semilla de dev y staging: competiciones y jornadas de competitions.csv y matchdays.csv
-- (docs/04-datos/datos-ejemplo/). No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.
-- Es repetible e idempotente: INSERT IGNORE sobre las claves únicas de las competiciones y las jornadas.
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Liga Insular Alevín masculina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Liga Insular Alevín femenina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Liga Insular Infantil masculina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Liga Insular Infantil femenina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Liga Insular Cadete masculina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Liga Insular Cadete femenina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Liga Insular Juvenil masculina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Liga Insular Juvenil femenina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Liga Insular Sénior masculina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO competitions (season_id, category_id, name, federation, forfeit_penalty_points, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Liga Insular Sénior femenina', 'Federación Isleña de Voleibol (ficticia)', 1, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND c.name = 'Liga Insular Alevín masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND c.name = 'Liga Insular Alevín femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND c.name = 'Liga Insular Infantil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Infantil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 11, '2027-03-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 12, '2027-03-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 13, '2027-04-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 14, '2027-04-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND c.name = 'Liga Insular Cadete masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 11, '2027-03-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 12, '2027-03-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 13, '2027-04-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 14, '2027-04-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND c.name = 'Liga Insular Cadete femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND c.name = 'Liga Insular Juvenil masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND c.name = 'Liga Insular Juvenil femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 11, '2027-03-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 12, '2027-03-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 13, '2027-04-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 14, '2027-04-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND c.name = 'Liga Insular Sénior masculina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 1, '2026-10-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 2, '2026-10-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 3, '2026-10-31', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 4, '2026-11-14', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 5, '2026-11-28', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 6, '2026-12-12', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 7, '2027-01-09', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 8, '2027-01-23', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 9, '2027-02-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 10, '2027-02-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 11, '2027-03-06', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 12, '2027-03-20', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 13, '2027-04-03', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
INSERT IGNORE INTO matchdays (competition_id, number, scheduled_date, created_at, updated_at)
  SELECT c.id, 14, '2027-04-17', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6) FROM competitions c WHERE c.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND c.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND c.name = 'Liga Insular Sénior femenina';
