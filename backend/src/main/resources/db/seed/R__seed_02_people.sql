-- Semilla de dev y staging: los jugadores y entrenadores de docs/04-datos/datos-ejemplo/people.csv.
-- No se edita a mano: la escribe scripts/datos-ejemplo/semillas.mjs. Es repetible e idempotente.
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tanausú', 'Ramos Suárez', '2015-08-24', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tanausú' AND last_name = 'Ramos Suárez' AND birth_date <=> '2015-08-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Iván', 'Toledo Reyes', '2016-12-15', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Iván' AND last_name = 'Toledo Reyes' AND birth_date <=> '2016-12-15');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Alejandro', 'Marrero Cabrera', '2016-05-05', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Alejandro' AND last_name = 'Marrero Cabrera' AND birth_date <=> '2016-05-05');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Manuel', 'Castro Febles', '2016-02-02', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Manuel' AND last_name = 'Castro Febles' AND birth_date <=> '2016-02-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Adrián', 'Linares Rosales', '2016-10-04', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Adrián' AND last_name = 'Linares Rosales' AND birth_date <=> '2016-10-04');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Acaymo', 'Afonso García', '2016-08-20', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Acaymo' AND last_name = 'Afonso García' AND birth_date <=> '2016-08-20');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Raúl', 'Vera Cabrera', '2015-12-28', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Raúl' AND last_name = 'Vera Cabrera' AND birth_date <=> '2015-12-28');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Afonso Febles', '2016-04-15', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Afonso Febles' AND birth_date <=> '2016-04-15');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Guillermo', 'Vera Pérez', '2016-07-06', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Guillermo' AND last_name = 'Vera Pérez' AND birth_date <=> '2016-07-06');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Carlos', 'Marrero Cabrera', '2015-01-11', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Carlos' AND last_name = 'Marrero Cabrera' AND birth_date <=> '2015-01-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Ainhoa', 'Padilla Rosales', '2015-11-11', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Ainhoa' AND last_name = 'Padilla Rosales' AND birth_date <=> '2015-11-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Yaiza', 'Martín Marrero', '2015-01-13', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Yaiza' AND last_name = 'Martín Marrero' AND birth_date <=> '2015-01-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Gara', 'Vera Alonso', '2015-10-14', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Gara' AND last_name = 'Vera Alonso' AND birth_date <=> '2015-10-14');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Raquel', 'Martín Linares', '2015-04-26', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Raquel' AND last_name = 'Martín Linares' AND birth_date <=> '2015-04-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Guacimara', 'Hernández Pérez', '2015-05-21', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Guacimara' AND last_name = 'Hernández Pérez' AND birth_date <=> '2015-05-21');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nerea', 'Siverio Alonso', '2016-01-02', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nerea' AND last_name = 'Siverio Alonso' AND birth_date <=> '2016-01-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Zaida', 'Castro Toledo', '2015-05-05', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Zaida' AND last_name = 'Castro Toledo' AND birth_date <=> '2015-05-05');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Atteneri', 'Martín Cabrera', '2015-11-10', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Atteneri' AND last_name = 'Martín Cabrera' AND birth_date <=> '2015-11-10');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Noelia', 'Luis Barrios', '2016-08-04', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Noelia' AND last_name = 'Luis Barrios' AND birth_date <=> '2016-08-04');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Irene', 'Padilla Díaz', '2016-08-03', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Irene' AND last_name = 'Padilla Díaz' AND birth_date <=> '2016-08-03');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Carlos', 'Siverio Santana', '2013-07-03', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Carlos' AND last_name = 'Siverio Santana' AND birth_date <=> '2013-07-03');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tinerfe', 'Vera Martín', '2013-05-10', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tinerfe' AND last_name = 'Vera Martín' AND birth_date <=> '2013-05-10');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Yeray', 'Suárez Hernández', '2014-02-28', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Yeray' AND last_name = 'Suárez Hernández' AND birth_date <=> '2014-02-28');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Airam', 'Linares García', '2013-12-04', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Airam' AND last_name = 'Linares García' AND birth_date <=> '2013-12-04');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Alejandro', 'Afonso Padilla', '2014-09-02', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Alejandro' AND last_name = 'Afonso Padilla' AND birth_date <=> '2014-09-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Hugo', 'Santana Rodríguez', '2013-01-26', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Hugo' AND last_name = 'Santana Rodríguez' AND birth_date <=> '2013-01-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tanausú', 'Alonso Pérez', '2014-02-05', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tanausú' AND last_name = 'Alonso Pérez' AND birth_date <=> '2014-02-05');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Ayoze', 'Padilla García', '2013-01-28', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Ayoze' AND last_name = 'Padilla García' AND birth_date <=> '2013-01-28');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tanausú', 'Toledo Siverio', '2014-01-14', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tanausú' AND last_name = 'Toledo Siverio' AND birth_date <=> '2014-01-14');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Leo', 'Rosales Pérez', '2014-05-17', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Leo' AND last_name = 'Rosales Pérez' AND birth_date <=> '2014-05-17');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Guillermo', 'Castro Luis', '2013-08-12', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Guillermo' AND last_name = 'Castro Luis' AND birth_date <=> '2013-08-12');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Hugo', 'Bethencourt Padilla', '2014-10-20', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Hugo' AND last_name = 'Bethencourt Padilla' AND birth_date <=> '2014-10-20');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Paula', 'Padilla Ramos', '2014-06-15', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Paula' AND last_name = 'Padilla Ramos' AND birth_date <=> '2014-06-15');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Cathaysa', 'Mesa Trujillo', '2014-10-08', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Cathaysa' AND last_name = 'Mesa Trujillo' AND birth_date <=> '2014-10-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nayra', 'Rosales Trujillo', '2013-02-09', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nayra' AND last_name = 'Rosales Trujillo' AND birth_date <=> '2013-02-09');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nerea', 'García Delgado', '2014-11-09', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nerea' AND last_name = 'García Delgado' AND birth_date <=> '2014-11-09');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Noelia', 'Linares Febles', '2014-03-19', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Noelia' AND last_name = 'Linares Febles' AND birth_date <=> '2014-03-19');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Jennifer', 'Siverio Barrios', '2013-07-02', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Jennifer' AND last_name = 'Siverio Barrios' AND birth_date <=> '2013-07-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Idaira', 'Toledo Marrero', '2013-10-09', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Idaira' AND last_name = 'Toledo Marrero' AND birth_date <=> '2013-10-09');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Paula', 'Toledo Morales', '2014-08-08', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Paula' AND last_name = 'Toledo Morales' AND birth_date <=> '2014-08-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nayra', 'Suárez Luis', '2013-04-08', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nayra' AND last_name = 'Suárez Luis' AND birth_date <=> '2013-04-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Gara', 'Alonso Herrera', '2014-02-09', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Gara' AND last_name = 'Alonso Herrera' AND birth_date <=> '2014-02-09');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'González Vera', '2013-08-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'González Vera' AND birth_date <=> '2013-08-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Irene', 'Barrios Mesa', '2014-10-22', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Irene' AND last_name = 'Barrios Mesa' AND birth_date <=> '2014-10-22');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Raúl', 'Pérez Afonso', '2012-10-03', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Raúl' AND last_name = 'Pérez Afonso' AND birth_date <=> '2012-10-03');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'David', 'Trujillo Afonso', '2011-10-15', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'David' AND last_name = 'Trujillo Afonso' AND birth_date <=> '2011-10-15');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Castro Santana', '2011-01-27', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Castro Santana' AND birth_date <=> '2011-01-27');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tinerfe', 'Suárez Expósito', '2012-12-26', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tinerfe' AND last_name = 'Suárez Expósito' AND birth_date <=> '2012-12-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Airam', 'Perera Martín', '2012-03-05', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Airam' AND last_name = 'Perera Martín' AND birth_date <=> '2012-03-05');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Guillermo', 'Luis Rosales', '2011-08-03', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Guillermo' AND last_name = 'Luis Rosales' AND birth_date <=> '2011-08-03');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Rubén', 'Ramos García', '2012-11-22', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Rubén' AND last_name = 'Ramos García' AND birth_date <=> '2012-11-22');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Luis Bethencourt', '2012-06-04', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Luis Bethencourt' AND birth_date <=> '2012-06-04');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tanausú', 'González Medina', '2012-06-11', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tanausú' AND last_name = 'González Medina' AND birth_date <=> '2012-06-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Vera Herrera', '2012-10-27', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Vera Herrera' AND birth_date <=> '2012-10-27');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Yoel', 'Luis Padilla', '2012-03-12', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Yoel' AND last_name = 'Luis Padilla' AND birth_date <=> '2012-03-12');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Carlos', 'Bethencourt Febles', '2011-09-18', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Carlos' AND last_name = 'Bethencourt Febles' AND birth_date <=> '2011-09-18');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'Afonso Linares', '2011-04-27', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'Afonso Linares' AND birth_date <=> '2011-04-27');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Noelia', 'Bethencourt Afonso', '2011-07-12', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Noelia' AND last_name = 'Bethencourt Afonso' AND birth_date <=> '2011-07-12');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Sara', 'Rodríguez Padilla', '2012-11-01', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Sara' AND last_name = 'Rodríguez Padilla' AND birth_date <=> '2012-11-01');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Gara', 'Rosales Siverio', '2011-04-02', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Gara' AND last_name = 'Rosales Siverio' AND birth_date <=> '2011-04-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Jennifer', 'Vera Suárez', '2012-11-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Jennifer' AND last_name = 'Vera Suárez' AND birth_date <=> '2012-11-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Carla', 'Vera Martín', '2011-08-12', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Carla' AND last_name = 'Vera Martín' AND birth_date <=> '2011-08-12');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'María', 'Alonso Barrios', '2012-05-08', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'María' AND last_name = 'Alonso Barrios' AND birth_date <=> '2012-05-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'Mesa Ramos', '2012-05-16', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'Mesa Ramos' AND birth_date <=> '2012-05-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Claudia', 'Pérez Luis', '2011-04-01', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Claudia' AND last_name = 'Pérez Luis' AND birth_date <=> '2011-04-01');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Lucía', 'Rodríguez González', '2012-02-19', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Lucía' AND last_name = 'Rodríguez González' AND birth_date <=> '2012-02-19');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Jennifer', 'Ramos Barrios', '2011-08-06', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Jennifer' AND last_name = 'Ramos Barrios' AND birth_date <=> '2011-08-06');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Elena', 'Medina Linares', '2012-02-23', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Elena' AND last_name = 'Medina Linares' AND birth_date <=> '2012-02-23');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'David', 'Morales Perera', '2009-12-11', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'David' AND last_name = 'Morales Perera' AND birth_date <=> '2009-12-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Jonay', 'Linares Herrera', '2009-08-24', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Jonay' AND last_name = 'Linares Herrera' AND birth_date <=> '2009-08-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Álvaro', 'Siverio Suárez', '2009-11-16', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Álvaro' AND last_name = 'Siverio Suárez' AND birth_date <=> '2009-11-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Pablo', 'Febles Castro', '2010-03-16', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Pablo' AND last_name = 'Febles Castro' AND birth_date <=> '2010-03-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Hugo', 'Delgado Trujillo', '2009-03-24', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Hugo' AND last_name = 'Delgado Trujillo' AND birth_date <=> '2009-03-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'David', 'Herrera Afonso', '2010-03-19', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'David' AND last_name = 'Herrera Afonso' AND birth_date <=> '2010-03-19');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Barrios Padilla', '2010-11-26', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Barrios Padilla' AND birth_date <=> '2010-11-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Hernández Martín', '2009-06-02', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Hernández Martín' AND birth_date <=> '2009-06-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Ayoze', 'Rodríguez Rosales', '2010-03-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Ayoze' AND last_name = 'Rodríguez Rosales' AND birth_date <=> '2010-03-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Pablo', 'Cabrera Rodríguez', '2010-07-26', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Pablo' AND last_name = 'Cabrera Rodríguez' AND birth_date <=> '2010-07-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Kevin', 'Bethencourt Mesa', '2009-03-04', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Kevin' AND last_name = 'Bethencourt Mesa' AND birth_date <=> '2009-03-04');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Ayoze', 'Toledo Reyes', '2009-09-03', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Ayoze' AND last_name = 'Toledo Reyes' AND birth_date <=> '2009-09-03');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Sara', 'Delgado Ramos', '2009-02-18', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Sara' AND last_name = 'Delgado Ramos' AND birth_date <=> '2009-02-18');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Zaida', 'Martín Barrios', '2010-04-02', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Zaida' AND last_name = 'Martín Barrios' AND birth_date <=> '2010-04-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Andrea', 'Linares Alonso', '2010-12-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Andrea' AND last_name = 'Linares Alonso' AND birth_date <=> '2010-12-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Paula', 'Martín Trujillo', '2010-07-28', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Paula' AND last_name = 'Martín Trujillo' AND birth_date <=> '2010-07-28');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Zaida', 'Trujillo García', '2010-04-26', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Zaida' AND last_name = 'Trujillo García' AND birth_date <=> '2010-04-26');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Atteneri', 'García Díaz', '2010-03-21', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Atteneri' AND last_name = 'García Díaz' AND birth_date <=> '2010-03-21');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Chaxiraxi', 'Alonso Padilla', '2010-02-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Chaxiraxi' AND last_name = 'Alonso Padilla' AND birth_date <=> '2010-02-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Idaira', 'Delgado Linares', '2009-10-19', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Idaira' AND last_name = 'Delgado Linares' AND birth_date <=> '2009-10-19');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'Santana Castro', '2010-07-16', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'Santana Castro' AND birth_date <=> '2010-07-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Paula', 'Expósito Herrera', '2010-06-08', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Paula' AND last_name = 'Expósito Herrera' AND birth_date <=> '2010-06-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Idaira', 'Febles Reyes', '2009-02-05', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Idaira' AND last_name = 'Febles Reyes' AND birth_date <=> '2009-02-05');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Marta', 'Herrera Trujillo', '2010-08-02', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Marta' AND last_name = 'Herrera Trujillo' AND birth_date <=> '2010-08-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Guillermo', 'Suárez Febles', '2001-06-16', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Guillermo' AND last_name = 'Suárez Febles' AND birth_date <=> '2001-06-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Lucas', 'Díaz Alonso', '1990-08-11', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Lucas' AND last_name = 'Díaz Alonso' AND birth_date <=> '1990-08-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Acaymo', 'Castro Febles', '2000-12-27', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Acaymo' AND last_name = 'Castro Febles' AND birth_date <=> '2000-12-27');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Alejandro', 'Bethencourt Reyes', '1997-03-24', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Alejandro' AND last_name = 'Bethencourt Reyes' AND birth_date <=> '1997-03-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'García Linares', '1990-11-21', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'García Linares' AND birth_date <=> '1990-11-21');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Óscar', 'Martín Suárez', '2006-02-08', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Óscar' AND last_name = 'Martín Suárez' AND birth_date <=> '2006-02-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Afonso Trujillo', '2000-12-19', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Afonso Trujillo' AND birth_date <=> '2000-12-19');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Eduardo', 'Herrera Luis', '1994-07-16', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Eduardo' AND last_name = 'Herrera Luis' AND birth_date <=> '1994-07-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tinerfe', 'Pérez Perera', '1992-06-06', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tinerfe' AND last_name = 'Pérez Perera' AND birth_date <=> '1992-06-06');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Pablo', 'Díaz Reyes', '1993-03-01', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Pablo' AND last_name = 'Díaz Reyes' AND birth_date <=> '1993-03-01');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Lucas', 'Castro Pérez', '1994-11-13', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Lucas' AND last_name = 'Castro Pérez' AND birth_date <=> '1994-11-13');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Manuel', 'Rodríguez Díaz', '1992-04-02', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Manuel' AND last_name = 'Rodríguez Díaz' AND birth_date <=> '1992-04-02');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Marta', 'Martín Cabrera', '2004-05-24', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Marta' AND last_name = 'Martín Cabrera' AND birth_date <=> '2004-05-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Zaida', 'Díaz Herrera', '1991-02-06', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Zaida' AND last_name = 'Díaz Herrera' AND birth_date <=> '1991-02-06');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'González Expósito', '2002-10-08', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'González Expósito' AND birth_date <=> '2002-10-08');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Marta', 'Mesa Padilla', '2002-10-24', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Marta' AND last_name = 'Mesa Padilla' AND birth_date <=> '2002-10-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'María', 'Bethencourt Delgado', '2007-05-21', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'María' AND last_name = 'Bethencourt Delgado' AND birth_date <=> '2007-05-21');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Gara', 'Toledo Pérez', '2007-10-16', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Gara' AND last_name = 'Toledo Pérez' AND birth_date <=> '2007-10-16');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Yanira', 'Ramos Afonso', '2005-02-01', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Yanira' AND last_name = 'Ramos Afonso' AND birth_date <=> '2005-02-01');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nayra', 'Toledo Rosales', '2000-11-01', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nayra' AND last_name = 'Toledo Rosales' AND birth_date <=> '2000-11-01');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'María', 'Vera Herrera', '2001-12-24', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'María' AND last_name = 'Vera Herrera' AND birth_date <=> '2001-12-24');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Nayra', 'González Toledo', '1993-02-12', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Nayra' AND last_name = 'González Toledo' AND birth_date <=> '1993-02-12');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Elena', 'Rodríguez Alonso', '1997-04-11', NULL, FALSE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Elena' AND last_name = 'Rodríguez Alonso' AND birth_date <=> '1997-04-11');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tania', 'González Suárez', '2008-05-23', NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tania' AND last_name = 'González Suárez' AND birth_date <=> '2008-05-23');
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Yoel', 'Delgado Bethencourt', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Yoel' AND last_name = 'Delgado Bethencourt' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Zaida', 'Rodríguez Herrera', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Zaida' AND last_name = 'Rodríguez Herrera' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tamara', 'Medina Afonso', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tamara' AND last_name = 'Medina Afonso' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Tanausú', 'Siverio Alonso', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Tanausú' AND last_name = 'Siverio Alonso' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Raúl', 'Cabrera Reyes', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Raúl' AND last_name = 'Cabrera Reyes' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Hugo', 'Herrera Marrero', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Hugo' AND last_name = 'Herrera Marrero' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Acaymo', 'Linares González', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Acaymo' AND last_name = 'Linares González' AND birth_date <=> NULL);
INSERT INTO people (first_name, last_name, birth_date, photo_url, image_consent, created_at, updated_at)
  SELECT 'Acaymo', 'Martín Rosales', NULL, NULL, TRUE, UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)
  FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM people WHERE first_name = 'Acaymo' AND last_name = 'Martín Rosales' AND birth_date <=> NULL);
