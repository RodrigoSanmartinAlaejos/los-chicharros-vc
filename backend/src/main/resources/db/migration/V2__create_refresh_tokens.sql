-- Tokens de refresco de la sesión (diccionario de datos de la fase 1). Sin columnas de auditoría:
-- tiene sus propias fechas. La usa el login (sprint 2).
CREATE TABLE refresh_tokens (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT UNSIGNED NOT NULL,
  token_hash CHAR(64) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  expires_at DATETIME(6) NOT NULL,
  session_expires_at DATETIME(6) NOT NULL,
  revoked_at DATETIME(6) NULL,
  CONSTRAINT uk_refresh_tokens_token_hash UNIQUE (token_hash),
  KEY idx_refresh_tokens_user_id (user_id),
  CONSTRAINT fk_refresh_tokens_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
