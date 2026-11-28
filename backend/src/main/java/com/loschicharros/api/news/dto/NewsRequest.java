package com.loschicharros.api.news.dto;

import com.loschicharros.api.news.NewsType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Lo que se escribe en el editor de una noticia (contrato OpenAPI, {@code NewsRequest}). El cuerpo
 * va en Markdown. Publicar o retirar no va aquí: es un {@code PATCH}. Si {@code featured} no llega,
 * la noticia no es destacada.
 */
public record NewsRequest(
    @NotBlank @Size(max = 150) String title,
    @NotBlank @Size(max = 300) String summary,
    @NotBlank @Size(max = 50_000) String content,
    @NotNull NewsType type,
    Boolean featured) {

  /** Solo es destacada si llega {@code true}. */
  public boolean isFeatured() {
    return Boolean.TRUE.equals(featured);
  }
}
