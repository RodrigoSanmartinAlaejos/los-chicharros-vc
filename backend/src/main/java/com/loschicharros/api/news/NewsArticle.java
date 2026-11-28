package com.loschicharros.api.news;

import com.loschicharros.api.common.audit.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Noticia del club. No tiene columna de estado (diagrama «Estados de una noticia»): sin {@code
 * publishedAt} es un borrador, con fecha está publicada (y se ve en la web desde esa fecha), y con
 * {@code deletedAt} está borrada. El cuerpo se guarda en Markdown.
 */
@Entity
@Table(name = "news_articles")
public class NewsArticle extends Auditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String title;

  @Column(nullable = false, length = 170, unique = true)
  private String slug;

  @Column(nullable = false, length = 300)
  private String summary;

  @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
  private String content;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private NewsType type;

  @Column(nullable = false)
  private boolean featured;

  @Column(name = "published_at")
  private Instant publishedAt;

  @Column(name = "deleted_at")
  private Instant deletedAt;

  protected NewsArticle() {}

  public NewsArticle(
      String title, String slug, String summary, String content, NewsType type, boolean featured) {
    this.slug = slug;
    update(title, summary, content, type, featured);
  }

  /** Cambia lo que se escribe en el editor (el slug y la publicación van aparte). */
  public void update(
      String title, String summary, String content, NewsType type, boolean featured) {
    this.title = title;
    this.summary = summary;
    this.content = content;
    this.type = type;
    this.featured = featured;
  }

  public void changeSlug(String slug) {
    this.slug = slug;
  }

  /** Publica en esa fecha, o vuelve a borrador con {@code null}. */
  public void publishAt(Instant when) {
    this.publishedAt = when;
  }

  public void markFeatured(boolean featured) {
    this.featured = featured;
  }

  /** Borrado lógico: desde ahora la noticia no aparece ni en la web ni en el panel. */
  public void markDeleted(Instant when) {
    this.deletedAt = when;
  }

  public Long getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getSlug() {
    return slug;
  }

  public String getSummary() {
    return summary;
  }

  public String getContent() {
    return content;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public NewsType getType() {
    return type;
  }

  public boolean isFeatured() {
    return featured;
  }

  public Instant getPublishedAt() {
    return publishedAt;
  }

  public boolean isDraft() {
    return publishedAt == null;
  }
}
