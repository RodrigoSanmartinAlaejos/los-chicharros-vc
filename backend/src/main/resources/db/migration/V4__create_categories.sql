-- Categorías por edad y sexo (diccionario de datos de la fase 1).
CREATE TABLE categories (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(30) NOT NULL,
  gender VARCHAR(30) NOT NULL,
  sort_order SMALLINT NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_categories_name_gender UNIQUE (name, gender),
  CONSTRAINT ck_categories_gender CHECK (gender IN ('MALE', 'FEMALE', 'MIXED')),
  KEY idx_categories_created_by (created_by),
  KEY idx_categories_updated_by (updated_by),
  CONSTRAINT fk_categories_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_categories_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
