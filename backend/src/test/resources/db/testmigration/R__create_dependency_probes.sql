-- Tabla de apoyo de los tests: filas que apuntan a una temporada, una categoría y una instalación,
-- como harán equipos, competiciones y partidos. Sirve para probar el 409 al borrar algo en uso
-- antes de que existan esas tablas.
CREATE TABLE IF NOT EXISTS dependency_probes (
  id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  season_id BIGINT UNSIGNED NULL,
  category_id BIGINT UNSIGNED NULL,
  venue_id BIGINT UNSIGNED NULL,
  KEY idx_dependency_probes_season_id (season_id),
  KEY idx_dependency_probes_category_id (category_id),
  KEY idx_dependency_probes_venue_id (venue_id),
  CONSTRAINT fk_dependency_probes_seasons FOREIGN KEY (season_id) REFERENCES seasons (id),
  CONSTRAINT fk_dependency_probes_categories FOREIGN KEY (category_id) REFERENCES categories (id),
  CONSTRAINT fk_dependency_probes_venues FOREIGN KEY (venue_id) REFERENCES venues (id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci;
