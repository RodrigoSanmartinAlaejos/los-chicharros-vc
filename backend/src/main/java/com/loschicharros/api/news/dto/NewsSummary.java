package com.loschicharros.api.news.dto;

import com.loschicharros.api.news.NewsArticle;
import com.loschicharros.api.news.NewsType;
import java.time.Instant;

/** Noticia sin el cuerpo, para los listados (contrato OpenAPI, {@code NewsSummary}). */
public record NewsSummary(
    Long id,
    String title,
    String slug,
    String summary,
    String imageUrl,
    NewsType type,
    boolean featured,
    Instant publishedAt) {

  public static NewsSummary from(NewsArticle article) {
    return new NewsSummary(
        article.getId(),
        article.getTitle(),
        article.getSlug(),
        article.getSummary(),
        article.getImageUrl(),
        article.getType(),
        article.isFeatured(),
        article.getPublishedAt());
  }
}
