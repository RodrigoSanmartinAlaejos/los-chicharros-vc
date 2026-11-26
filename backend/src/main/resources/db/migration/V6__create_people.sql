-- Jugadores y entrenadores del club (diccionario de datos de la fase 1). El borrado es lógico:
-- deleted_at oculta a la persona en el panel y conserva sus pertenencias y resultados.
CREATE TABLE people (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  first_name VARCHAR(60) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  birth_date DATE NULL,
  photo_url VARCHAR(500) NULL,
  image_consent BOOLEAN NOT NULL DEFAULT FALSE,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  KEY idx_people_last_name_first_name (last_name, first_name),
  KEY idx_people_created_by (created_by),
  KEY idx_people_updated_by (updated_by),
  CONSTRAINT fk_people_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_people_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
