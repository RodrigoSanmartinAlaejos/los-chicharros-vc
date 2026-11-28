-- Partidos de cada jornada (diccionario de datos de la fase 1). Fecha y hora en UTC.
CREATE TABLE matches (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  matchday_id BIGINT UNSIGNED NOT NULL,
  home_team_id BIGINT UNSIGNED NOT NULL,
  away_team_id BIGINT UNSIGNED NOT NULL,
  venue_id BIGINT UNSIGNED NULL,
  starts_at DATETIME(6) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
  home_sets TINYINT UNSIGNED NULL,
  away_sets TINYINT UNSIGNED NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT ck_matches_teams CHECK (home_team_id <> away_team_id),
  CONSTRAINT ck_matches_status CHECK (
    status IN ('SCHEDULED', 'IN_PROGRESS', 'FINISHED', 'POSTPONED', 'CANCELLED', 'FORFEITED')),
  -- Sin marcador, o uno llega a 3 y el otro se queda por debajo
  CONSTRAINT ck_matches_sets CHECK (
    (home_sets IS NULL AND away_sets IS NULL)
    OR (home_sets = 3 AND away_sets < 3)
    OR (away_sets = 3 AND home_sets < 3)),
  KEY idx_matches_starts_at (starts_at),
  KEY idx_matches_matchday_id (matchday_id),
  KEY idx_matches_home_team_id (home_team_id),
  KEY idx_matches_away_team_id (away_team_id),
  KEY idx_matches_venue_id (venue_id),
  KEY idx_matches_created_by (created_by),
  KEY idx_matches_updated_by (updated_by),
  CONSTRAINT fk_matches_matchdays FOREIGN KEY (matchday_id) REFERENCES matchdays (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_matches_home_team FOREIGN KEY (home_team_id) REFERENCES teams (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_matches_away_team FOREIGN KEY (away_team_id) REFERENCES teams (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_matches_venues FOREIGN KEY (venue_id) REFERENCES venues (id) ON DELETE RESTRICT,
  CONSTRAINT fk_matches_created_by FOREIGN KEY (created_by) REFERENCES users (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_matches_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
    ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
