package com.loschicharros.api.news;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Parte de la URL de una noticia a partir de su título: «¡Victoria en Güímar!» →
 * «victoria-en-guimar».
 */
final class Slugs {

  /** Hueco para el sufijo de desempate («-12») dentro de los 170 caracteres de la columna. */
  static final int MAX_BASE_LENGTH = 160;

  private Slugs() {}

  static String from(String title) {
    var plain =
        Normalizer.normalize(title, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("(^-+)|(-+$)", "");
    if (plain.length() > MAX_BASE_LENGTH) {
      plain = plain.substring(0, MAX_BASE_LENGTH).replaceAll("-[^-]*$", "");
    }
    return plain.isEmpty() ? "noticia" : plain;
  }
}
