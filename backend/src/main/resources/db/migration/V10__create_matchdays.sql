-- Jornadas de cada competición (diccionario de datos de la fase 1). Se borran con su competición.
CREATE TABLE matchdays (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  competition_id BIGINT UNSIGNED NOT NULL,
  number SMALLINT UNSIGNED NOT NULL,
  scheduled_date DATE NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_matchdays_competition_number UNIQUE (competition_id, number),
  CONSTRAINT ck_matchdays_number CHECK (number BETWEEN 1 AND 99),
  KEY idx_matchdays_created_by (created_by),
  KEY idx_matchdays_updated_by (updated_by),
  CONSTRAINT fk_matchdays_competitions FOREIGN KEY (competition_id) REFERENCES competitions (id)
    ON DELETE CASCADE,
  CONSTRAINT fk_matchdays_created_by FOREIGN KEY (created_by) REFERENCES users (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_matchdays_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
    ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
