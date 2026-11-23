package com.loschicharros.api.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Deja la base como recién migrada antes de cada test: vacía las tablas de negocio y, en las de
 * catálogo que carga una migración, borra solo las filas que añadieron los tests.
 *
 * <p>Las filas del catálogo no se restauran si un test las modifica: los tests crean sus propias
 * filas para editarlas o borrarlas (docs/07-desarrollo/guia-tests.md).
 */
@TestComponent
public class DatabaseCleaner {

  /** Tablas que rellena una migración y que los tests no deben vaciar. */
  private static final Set<String> KEEP = Set.of("flyway_schema_history", "roles", "categories");

  /** Tablas de catálogo con id: el último id que cargaron las migraciones. */
  private static final Set<String> CATALOGS = Set.of("roles", "categories");

  private final JdbcTemplate jdbc;
  private Map<String, Long> baseline;

  public DatabaseCleaner(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void clean() {
    var tables =
        jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables"
                + " WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
            String.class);
    if (baseline == null) {
      baseline = readBaseline(tables);
    }
    // Todas las sentencias van por la misma conexión: FOREIGN_KEY_CHECKS es de sesión.
    jdbc.execute(
        (ConnectionCallback<Void>)
            connection -> {
              try (var statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS = 0");
                for (var table : tables) {
                  if (!KEEP.contains(table)) {
                    statement.execute("TRUNCATE TABLE `" + table + "`");
                  } else if (baseline.containsKey(table)) {
                    statement.execute(
                        "DELETE FROM `" + table + "` WHERE id > " + baseline.get(table));
                  }
                }
                statement.execute("SET FOREIGN_KEY_CHECKS = 1");
              }
              return null;
            });
  }

  private Map<String, Long> readBaseline(Iterable<String> tables) {
    var ids = new HashMap<String, Long>();
    for (var table : tables) {
      if (CATALOGS.contains(table)) {
        var max = jdbc.queryForObject("SELECT MAX(id) FROM `" + table + "`", Long.class);
        ids.put(table, max == null ? 0L : max);
      }
    }
    return ids;
  }
}
