-- Instalaciones donde juega y entrena el club (diccionario de datos de la fase 1).
CREATE TABLE venues (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  address VARCHAR(200) NOT NULL,
  latitude DECIMAL(9, 6) NULL,
  longitude DECIMAL(9, 6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_venues_name UNIQUE (name),
  CONSTRAINT ck_venues_latitude CHECK (latitude BETWEEN -90 AND 90),
  CONSTRAINT ck_venues_longitude CHECK (longitude BETWEEN -180 AND 180),
  KEY idx_venues_created_by (created_by),
  KEY idx_venues_updated_by (updated_by),
  CONSTRAINT fk_venues_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_venues_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
