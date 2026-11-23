-- Temporadas (diccionario de datos de la fase 1). Que solo haya una activa lo controla el servicio.
CREATE TABLE seasons (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(9) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  active BOOLEAN NOT NULL DEFAULT FALSE,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_seasons_name UNIQUE (name),
  CONSTRAINT ck_seasons_dates CHECK (end_date > start_date),
  KEY idx_seasons_created_by (created_by),
  KEY idx_seasons_updated_by (updated_by),
  CONSTRAINT fk_seasons_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_seasons_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
