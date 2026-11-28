package com.loschicharros.api.news.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Publicar, retirar o destacar una noticia (contrato OpenAPI, {@code NewsPatchRequest}). Solo
 * cambia lo que llega, y {@code "publishedAt": null} (retirar) no es lo mismo que no enviarlo: por
 * eso no es un {@code record}, y cada setter apunta que el campo ha llegado.
 */
@Schema(description = "Solo se cambian los campos enviados.")
public class NewsPatchRequest {

  @Schema(
      description =
          "Fecha de publicación; `null` la retira (vuelve a borrador). Si es futura, la"
              + " noticia no se ve en la web hasta entonces.",
      types = {"string", "null"},
      format = "date-time")
  private Instant publishedAt;

  private Boolean featured;

  @Schema(hidden = true)
  private boolean publishedAtSent;

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public void setPublishedAt(Instant publishedAt) {
    this.publishedAt = publishedAt;
    this.publishedAtSent = true;
  }

  public Boolean getFeatured() {
    return featured;
  }

  public void setFeatured(Boolean featured) {
    this.featured = featured;
  }

  /** Si la petición trae {@code publishedAt}, aunque sea {@code null}. */
  @Schema(hidden = true)
  public boolean hasPublishedAt() {
    return publishedAtSent;
  }
}
