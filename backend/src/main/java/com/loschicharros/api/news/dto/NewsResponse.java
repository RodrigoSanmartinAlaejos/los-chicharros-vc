package com.loschicharros.api.news.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.loschicharros.api.news.NewsArticle;
import com.loschicharros.api.news.NewsType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

/**
 * Noticia completa (contrato OpenAPI, {@code NewsResponse}): el cuerpo en HTML saneado para
 * mostrarlo y, solo en el panel, el Markdown original para editarlo.
 */
public record NewsResponse(
    Long id,
    String title,
    String slug,
    String summary,
    String imageUrl,
    NewsType type,
    boolean featured,
    Instant publishedAt,
    @Schema(description = "Cuerpo en HTML saneado") String contentHtml,
    @Schema(description = "Solo para el administrador") @JsonInclude(JsonInclude.Include.NON_NULL)
        String contentMarkdown) {

  /** Para la web pública: sin el Markdown. */
  public static NewsResponse published(NewsArticle article, String contentHtml) {
    return of(article, contentHtml, null);
  }

  /** Para el panel: con el Markdown que se edita. */
  public static NewsResponse forEditor(NewsArticle article, String contentHtml) {
    return of(article, contentHtml, article.getContent());
  }

  private static NewsResponse of(NewsArticle article, String contentHtml, String markdown) {
    return new NewsResponse(
        article.getId(),
        article.getTitle(),
        article.getSlug(),
        article.getSummary(),
        article.getImageUrl(),
        article.getType(),
        article.isFeatured(),
        article.getPublishedAt(),
        contentHtml,
        markdown);
  }
}
