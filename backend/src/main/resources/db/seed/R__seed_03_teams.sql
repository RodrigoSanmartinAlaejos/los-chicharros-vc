-- Semilla de dev y staging: equipos y plantillas de teams.csv y team_memberships.csv
-- (docs/04-datos/datos-ejemplo/). No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs.
-- Es repetible e idempotente: INSERT IGNORE sobre las claves únicas de los equipos y las plantillas.
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Atlántico Norte Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE'), 'Club Voleibol Cardonal Alto', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Atlántico Norte Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE'), 'Club Voleibol Cardonal Alto', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Atlántico Norte Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE'), 'Club Voleibol Cardonal Alto', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Los Chicharros V.C.', TRUE, '/assets/imagenes/equipo.svg', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Club Voleibol Alisios', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Vóley Barlovento', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Club Voleibol Malpaís', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Roque Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Club Voleibol Tabaiba', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Atlántico Norte Vóley', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO teams (season_id, category_id, name, own_club, crest_url, created_at, updated_at)
  SELECT (SELECT id FROM seasons WHERE name = '2026-27'), (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE'), 'Club Voleibol Cardonal Alto', FALSE, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6);
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 8, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tanausú' AND p.last_name = 'Ramos Suárez' AND p.birth_date <=> '2015-08-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Iván' AND p.last_name = 'Toledo Reyes' AND p.birth_date <=> '2016-12-15' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Alejandro' AND p.last_name = 'Marrero Cabrera' AND p.birth_date <=> '2016-05-05' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Manuel' AND p.last_name = 'Castro Febles' AND p.birth_date <=> '2016-02-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 11, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Adrián' AND p.last_name = 'Linares Rosales' AND p.birth_date <=> '2016-10-04' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Acaymo' AND p.last_name = 'Afonso García' AND p.birth_date <=> '2016-08-20' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Raúl' AND p.last_name = 'Vera Cabrera' AND p.birth_date <=> '2015-12-28' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 1, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Afonso Febles' AND p.birth_date <=> '2016-04-15' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Guillermo' AND p.last_name = 'Vera Pérez' AND p.birth_date <=> '2016-07-06' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Carlos' AND p.last_name = 'Marrero Cabrera' AND p.birth_date <=> '2015-01-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Ainhoa' AND p.last_name = 'Padilla Rosales' AND p.birth_date <=> '2015-11-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yaiza' AND p.last_name = 'Martín Marrero' AND p.birth_date <=> '2015-01-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Gara' AND p.last_name = 'Vera Alonso' AND p.birth_date <=> '2015-10-14' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Raquel' AND p.last_name = 'Martín Linares' AND p.birth_date <=> '2015-04-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Guacimara' AND p.last_name = 'Hernández Pérez' AND p.birth_date <=> '2015-05-21' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nerea' AND p.last_name = 'Siverio Alonso' AND p.birth_date <=> '2016-01-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Castro Toledo' AND p.birth_date <=> '2015-05-05' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 12, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Atteneri' AND p.last_name = 'Martín Cabrera' AND p.birth_date <=> '2015-11-10' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Noelia' AND p.last_name = 'Luis Barrios' AND p.birth_date <=> '2016-08-04' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 15, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Irene' AND p.last_name = 'Padilla Díaz' AND p.birth_date <=> '2016-08-03' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Carlos' AND p.last_name = 'Siverio Santana' AND p.birth_date <=> '2013-07-03' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 2, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tinerfe' AND p.last_name = 'Vera Martín' AND p.birth_date <=> '2013-05-10' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yeray' AND p.last_name = 'Suárez Hernández' AND p.birth_date <=> '2014-02-28' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 1, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Airam' AND p.last_name = 'Linares García' AND p.birth_date <=> '2013-12-04' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Alejandro' AND p.last_name = 'Afonso Padilla' AND p.birth_date <=> '2014-09-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Hugo' AND p.last_name = 'Santana Rodríguez' AND p.birth_date <=> '2013-01-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tanausú' AND p.last_name = 'Alonso Pérez' AND p.birth_date <=> '2014-02-05' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Ayoze' AND p.last_name = 'Padilla García' AND p.birth_date <=> '2013-01-28' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 15, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tanausú' AND p.last_name = 'Toledo Siverio' AND p.birth_date <=> '2014-01-14' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 8, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Leo' AND p.last_name = 'Rosales Pérez' AND p.birth_date <=> '2014-05-17' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Guillermo' AND p.last_name = 'Castro Luis' AND p.birth_date <=> '2013-08-12' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Hugo' AND p.last_name = 'Bethencourt Padilla' AND p.birth_date <=> '2014-10-20' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 11, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Paula' AND p.last_name = 'Padilla Ramos' AND p.birth_date <=> '2014-06-15' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Cathaysa' AND p.last_name = 'Mesa Trujillo' AND p.birth_date <=> '2014-10-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nayra' AND p.last_name = 'Rosales Trujillo' AND p.birth_date <=> '2013-02-09' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nerea' AND p.last_name = 'García Delgado' AND p.birth_date <=> '2014-11-09' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 17, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Noelia' AND p.last_name = 'Linares Febles' AND p.birth_date <=> '2014-03-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Jennifer' AND p.last_name = 'Siverio Barrios' AND p.birth_date <=> '2013-07-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Idaira' AND p.last_name = 'Toledo Marrero' AND p.birth_date <=> '2013-10-09' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 2, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Paula' AND p.last_name = 'Toledo Morales' AND p.birth_date <=> '2014-08-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nayra' AND p.last_name = 'Suárez Luis' AND p.birth_date <=> '2013-04-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Gara' AND p.last_name = 'Alonso Herrera' AND p.birth_date <=> '2014-02-09' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 15, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'González Vera' AND p.birth_date <=> '2013-08-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 14, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Irene' AND p.last_name = 'Barrios Mesa' AND p.birth_date <=> '2014-10-22' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Raúl' AND p.last_name = 'Pérez Afonso' AND p.birth_date <=> '2012-10-03' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'David' AND p.last_name = 'Trujillo Afonso' AND p.birth_date <=> '2011-10-15' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Castro Santana' AND p.birth_date <=> '2011-01-27' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 13, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tinerfe' AND p.last_name = 'Suárez Expósito' AND p.birth_date <=> '2012-12-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Airam' AND p.last_name = 'Perera Martín' AND p.birth_date <=> '2012-03-05' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Guillermo' AND p.last_name = 'Luis Rosales' AND p.birth_date <=> '2011-08-03' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 1, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Rubén' AND p.last_name = 'Ramos García' AND p.birth_date <=> '2012-11-22' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 15, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Luis Bethencourt' AND p.birth_date <=> '2012-06-04' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tanausú' AND p.last_name = 'González Medina' AND p.birth_date <=> '2012-06-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 14, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Vera Herrera' AND p.birth_date <=> '2012-10-27' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 11, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yoel' AND p.last_name = 'Luis Padilla' AND p.birth_date <=> '2012-03-12' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Carlos' AND p.last_name = 'Bethencourt Febles' AND p.birth_date <=> '2011-09-18' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 17, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'Afonso Linares' AND p.birth_date <=> '2011-04-27' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Noelia' AND p.last_name = 'Bethencourt Afonso' AND p.birth_date <=> '2011-07-12' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Sara' AND p.last_name = 'Rodríguez Padilla' AND p.birth_date <=> '2012-11-01' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Gara' AND p.last_name = 'Rosales Siverio' AND p.birth_date <=> '2011-04-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 14, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Jennifer' AND p.last_name = 'Vera Suárez' AND p.birth_date <=> '2012-11-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Carla' AND p.last_name = 'Vera Martín' AND p.birth_date <=> '2011-08-12' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'María' AND p.last_name = 'Alonso Barrios' AND p.birth_date <=> '2012-05-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'Mesa Ramos' AND p.birth_date <=> '2012-05-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 12, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Claudia' AND p.last_name = 'Pérez Luis' AND p.birth_date <=> '2011-04-01' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Lucía' AND p.last_name = 'Rodríguez González' AND p.birth_date <=> '2012-02-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Jennifer' AND p.last_name = 'Ramos Barrios' AND p.birth_date <=> '2011-08-06' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 11, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Elena' AND p.last_name = 'Medina Linares' AND p.birth_date <=> '2012-02-23' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'David' AND p.last_name = 'Morales Perera' AND p.birth_date <=> '2009-12-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 7, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Jonay' AND p.last_name = 'Linares Herrera' AND p.birth_date <=> '2009-08-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Álvaro' AND p.last_name = 'Siverio Suárez' AND p.birth_date <=> '2009-11-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Pablo' AND p.last_name = 'Febles Castro' AND p.birth_date <=> '2010-03-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Hugo' AND p.last_name = 'Delgado Trujillo' AND p.birth_date <=> '2009-03-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'David' AND p.last_name = 'Herrera Afonso' AND p.birth_date <=> '2010-03-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 14, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Barrios Padilla' AND p.birth_date <=> '2010-11-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Hernández Martín' AND p.birth_date <=> '2009-06-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Ayoze' AND p.last_name = 'Rodríguez Rosales' AND p.birth_date <=> '2010-03-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Pablo' AND p.last_name = 'Cabrera Rodríguez' AND p.birth_date <=> '2010-07-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 2, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Kevin' AND p.last_name = 'Bethencourt Mesa' AND p.birth_date <=> '2009-03-04' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Ayoze' AND p.last_name = 'Toledo Reyes' AND p.birth_date <=> '2009-09-03' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Sara' AND p.last_name = 'Delgado Ramos' AND p.birth_date <=> '2009-02-18' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Martín Barrios' AND p.birth_date <=> '2010-04-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 12, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Andrea' AND p.last_name = 'Linares Alonso' AND p.birth_date <=> '2010-12-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Paula' AND p.last_name = 'Martín Trujillo' AND p.birth_date <=> '2010-07-28' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Trujillo García' AND p.birth_date <=> '2010-04-26' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Atteneri' AND p.last_name = 'García Díaz' AND p.birth_date <=> '2010-03-21' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Chaxiraxi' AND p.last_name = 'Alonso Padilla' AND p.birth_date <=> '2010-02-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 7, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Idaira' AND p.last_name = 'Delgado Linares' AND p.birth_date <=> '2009-10-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 4, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'Santana Castro' AND p.birth_date <=> '2010-07-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 8, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Paula' AND p.last_name = 'Expósito Herrera' AND p.birth_date <=> '2010-06-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Idaira' AND p.last_name = 'Febles Reyes' AND p.birth_date <=> '2009-02-05' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 17, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Marta' AND p.last_name = 'Herrera Trujillo' AND p.birth_date <=> '2010-08-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Guillermo' AND p.last_name = 'Suárez Febles' AND p.birth_date <=> '2001-06-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 2, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Lucas' AND p.last_name = 'Díaz Alonso' AND p.birth_date <=> '1990-08-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 16, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Acaymo' AND p.last_name = 'Castro Febles' AND p.birth_date <=> '2000-12-27' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 8, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Alejandro' AND p.last_name = 'Bethencourt Reyes' AND p.birth_date <=> '1997-03-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'García Linares' AND p.birth_date <=> '1990-11-21' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Óscar' AND p.last_name = 'Martín Suárez' AND p.birth_date <=> '2006-02-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 1, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Afonso Trujillo' AND p.birth_date <=> '2000-12-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 12, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Herrera Luis' AND p.birth_date <=> '1994-07-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 17, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tinerfe' AND p.last_name = 'Pérez Perera' AND p.birth_date <=> '1992-06-06' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 14, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Pablo' AND p.last_name = 'Díaz Reyes' AND p.birth_date <=> '1993-03-01' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 7, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Lucas' AND p.last_name = 'Castro Pérez' AND p.birth_date <=> '1994-11-13' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 15, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Manuel' AND p.last_name = 'Rodríguez Díaz' AND p.birth_date <=> '1992-04-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 3, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Marta' AND p.last_name = 'Martín Cabrera' AND p.birth_date <=> '2004-05-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 2, 'colocador', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Díaz Herrera' AND p.birth_date <=> '1991-02-06' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 9, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'González Expósito' AND p.birth_date <=> '2002-10-08' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 10, 'opuesto', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Marta' AND p.last_name = 'Mesa Padilla' AND p.birth_date <=> '2002-10-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 8, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'María' AND p.last_name = 'Bethencourt Delgado' AND p.birth_date <=> '2007-05-21' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 5, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Gara' AND p.last_name = 'Toledo Pérez' AND p.birth_date <=> '2007-10-16' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 6, 'central', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yanira' AND p.last_name = 'Ramos Afonso' AND p.birth_date <=> '2005-02-01' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 18, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nayra' AND p.last_name = 'Toledo Rosales' AND p.birth_date <=> '2000-11-01' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 12, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'María' AND p.last_name = 'Vera Herrera' AND p.birth_date <=> '2001-12-24' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 20, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Nayra' AND p.last_name = 'González Toledo' AND p.birth_date <=> '1993-02-12' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 11, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Elena' AND p.last_name = 'Rodríguez Alonso' AND p.birth_date <=> '1997-04-11' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 19, 'líbero', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tania' AND p.last_name = 'González Suárez' AND p.birth_date <=> '2008-05-23' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 21, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Eduardo' AND p.last_name = 'Hernández Martín' AND p.birth_date <=> '2009-06-02' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'PLAYER', 21, 'receptor', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Idaira' AND p.last_name = 'Delgado Linares' AND p.birth_date <=> '2009-10-19' AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yoel' AND p.last_name = 'Delgado Bethencourt' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Alevín' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Yoel' AND p.last_name = 'Delgado Bethencourt' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Rodríguez Herrera' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Infantil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Zaida' AND p.last_name = 'Rodríguez Herrera' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tamara' AND p.last_name = 'Medina Afonso' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Cadete' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Tanausú' AND p.last_name = 'Siverio Alonso' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Raúl' AND p.last_name = 'Cabrera Reyes' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Juvenil' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Hugo' AND p.last_name = 'Herrera Marrero' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'MALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Acaymo' AND p.last_name = 'Linares González' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
INSERT IGNORE INTO team_memberships (team_id, person_id, type, shirt_number, position, created_at, updated_at)
  SELECT t.id, p.id, 'COACH', NULL, NULL, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM teams t, people p WHERE t.season_id = (SELECT id FROM seasons WHERE name = '2026-27') AND t.category_id = (SELECT id FROM categories WHERE name = 'Sénior' AND gender = 'FEMALE') AND t.name = 'Los Chicharros V.C.' AND p.first_name = 'Acaymo' AND p.last_name = 'Martín Rosales' AND p.birth_date <=> NULL AND p.deleted_at IS NULL;
