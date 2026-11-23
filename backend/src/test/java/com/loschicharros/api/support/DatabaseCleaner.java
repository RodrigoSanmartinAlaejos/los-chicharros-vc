package com.loschicharros.api.support;

import java.util.Set;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;

/** Vacía las tablas de negocio antes de cada test; deja las de catálogo que carga una migración. */
@TestComponent
public class DatabaseCleaner {

  /** Tablas que rellena una migración y que los tests no deben vaciar. */
  private static final Set<String> KEEP = Set.of("flyway_schema_history", "roles", "categories");

  private final JdbcTemplate jdbc;

  public DatabaseCleaner(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  public void clean() {
    var tables =
        jdbc.queryForList(
            "SELECT table_name FROM information_schema.tables"
                + " WHERE table_schema = DATABASE() AND table_type = 'BASE TABLE'",
            String.class);
    // Todas las sentencias van por la misma conexión: FOREIGN_KEY_CHECKS es de sesión.
    jdbc.execute(
        (ConnectionCallback<Void>)
            connection -> {
              try (var statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS = 0");
                for (var table : tables) {
                  if (!KEEP.contains(table)) {
                    statement.execute("TRUNCATE TABLE `" + table + "`");
                  }
                }
                statement.execute("SET FOREIGN_KEY_CHECKS = 1");
              }
              return null;
            });
  }
}
