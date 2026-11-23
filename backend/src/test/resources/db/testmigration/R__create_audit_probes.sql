-- Tabla de apoyo de los tests (solo existe en el perfil test): una entidad mínima con las columnas
-- de auditoría del diccionario de datos, para probar la clase base y el manejo de errores.
CREATE TABLE IF NOT EXISTS audit_probes (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(60) NOT NULL,
  code VARCHAR(5) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_audit_probes_code UNIQUE (code)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
