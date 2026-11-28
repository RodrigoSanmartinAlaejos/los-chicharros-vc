-- Noticias del club (diccionario de datos de la fase 1). No hay columna de estado: sin published_at
-- es un borrador, y deleted_at es el borrado lógico (diagrama «Estados de una noticia»). El slug no
-- se reutiliza nunca, ni el de una noticia borrada, para que un enlace antiguo no lleve a otra.
CREATE TABLE news_articles (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(150) NOT NULL,
  slug VARCHAR(170) NOT NULL,
  summary VARCHAR(300) NOT NULL,
  content MEDIUMTEXT NOT NULL,
  image_url VARCHAR(500) NULL,
  type VARCHAR(30) NOT NULL,
  featured BOOLEAN NOT NULL DEFAULT FALSE,
  published_at DATETIME(6) NULL,
  deleted_at DATETIME(6) NULL,
  created_at DATETIME(6) NOT NULL,
  updated_at DATETIME(6) NOT NULL,
  created_by BIGINT UNSIGNED NULL,
  updated_by BIGINT UNSIGNED NULL,
  CONSTRAINT uk_news_articles_slug UNIQUE (slug),
  CONSTRAINT ck_news_articles_type CHECK (type IN ('MATCH_REPORT', 'EVENT', 'STATEMENT')),
  KEY idx_news_articles_published_at (published_at),
  KEY idx_news_articles_created_by (created_by),
  KEY idx_news_articles_updated_by (updated_by),
  CONSTRAINT fk_news_articles_created_by FOREIGN KEY (created_by) REFERENCES users (id)
    ON DELETE RESTRICT,
  CONSTRAINT fk_news_articles_updated_by FOREIGN KEY (updated_by) REFERENCES users (id)
    ON DELETE RESTRICT
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
