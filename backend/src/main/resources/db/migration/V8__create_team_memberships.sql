-- Plantillas: qué personas juegan o entrenan en cada equipo (diccionario de datos de la fase 1).
-- El dorsal es único dentro del equipo; los NULL (entrenadores, jugadores sin dorsal) no chocan.
CREATE TABLE team_memberships (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  team_id BIGINT UNSIGNED NOT NULL,
  person_id BIGINT UNSIGNED NOT NULL,
  type VARCHAR(30) NOT NULL,
  shirt_number TINYINT UNSIGNED NULL,
  position VARCHAR(30) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_team_memberships_team_person_type UNIQUE (team_id, person_id, type),
  CONSTRAINT uk_team_memberships_team_shirt UNIQUE (team_id, shirt_number),
  CONSTRAINT ck_team_memberships_type CHECK (type IN ('PLAYER', 'COACH')),
  CONSTRAINT ck_team_memberships_shirt_number CHECK (shirt_number BETWEEN 0 AND 99),
  CONSTRAINT ck_team_memberships_coach_without_shirt
    CHECK (type = 'PLAYER' OR (shirt_number IS NULL AND position IS NULL)),
  KEY idx_team_memberships_person_id (person_id),
  KEY idx_team_memberships_created_by (created_by),
  KEY idx_team_memberships_updated_by (updated_by),
  CONSTRAINT fk_team_memberships_teams FOREIGN KEY (team_id) REFERENCES teams (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_team_memberships_people FOREIGN KEY (person_id) REFERENCES people (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_team_memberships_created_by FOREIGN KEY (created_by) REFERENCES users (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_team_memberships_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
    ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
