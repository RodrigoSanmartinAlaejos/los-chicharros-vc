-- Usuarios que inician sesión (diccionario de datos de la fase 1). Va la primera porque todas las
-- tablas apuntan a ella con created_by y updated_by. En la fase 1 solo existe el administrador.
CREATE TABLE users (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(254) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_users_email UNIQUE (email),
  KEY idx_users_created_by (created_by),
  KEY idx_users_updated_by (updated_by),
  CONSTRAINT fk_users_created_by FOREIGN KEY (created_by) REFERENCES users (id) ON DELETE RESTRICT,
  CONSTRAINT fk_users_updated_by FOREIGN KEY (updated_by) REFERENCES users (id) ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
