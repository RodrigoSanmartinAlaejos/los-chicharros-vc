-- Clasificación de cada competición (diccionario de datos de la fase 1). En la fase 1 se edita a
-- mano; desde la fase 3 la recalcula el servicio. Se borra con su competición.
CREATE TABLE standings (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  competition_id BIGINT UNSIGNED NOT NULL,
  team_id BIGINT UNSIGNED NOT NULL,
  points SMALLINT NOT NULL DEFAULT 0,
  played SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  won SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  lost SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  sets_won SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  sets_lost SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  penalty_points SMALLINT UNSIGNED NOT NULL DEFAULT 0,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_standings_competition_team UNIQUE (competition_id, team_id),
  KEY idx_standings_team_id (team_id),
  KEY idx_standings_created_by (created_by),
  KEY idx_standings_updated_by (updated_by),
  CONSTRAINT fk_standings_competitions FOREIGN KEY (competition_id) REFERENCES competitions (id)
    ON DELETE CASCADE,
  CONSTRAINT fk_standings_teams FOREIGN KEY (team_id) REFERENCES teams (id) ON DELETE RESTRICT,
  CONSTRAINT fk_standings_created_by FOREIGN KEY (created_by) REFERENCES users (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_standings_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
    ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
