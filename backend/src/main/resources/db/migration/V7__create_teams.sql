-- Equipos del club y rivales de cada temporada y categoría (diccionario de datos de la fase 1).
CREATE TABLE teams (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  season_id BIGINT UNSIGNED NOT NULL,
  category_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(100) NOT NULL,
  own_club BOOLEAN NOT NULL DEFAULT FALSE,
  crest_url VARCHAR(500) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_teams_season_category_name UNIQUE (season_id, category_id, name),
  KEY idx_teams_category_id (category_id),
  KEY idx_teams_created_by (created_by),
  KEY idx_teams_updated_by (updated_by),
  CONSTRAINT fk_teams_seasons FOREIGN KEY (season_id) REFERENCES seasons (id) ON DELETE RESTRICT,
  CONSTRAINT fk_teams_categories FOREIGN KEY (category_id) REFERENCES categories (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_teams_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_teams_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
