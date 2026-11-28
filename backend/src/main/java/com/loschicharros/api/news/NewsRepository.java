package com.loschicharros.api.news;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface NewsRepository extends JpaRepository<NewsArticle, Long> {

  /** Una noticia que no está borrada. */
  Optional<NewsArticle> findByIdAndDeletedAtIsNull(Long id);

  /** Una noticia que se ve en la web: sin borrar y publicada como muy tarde en {@code now}. */
  @Query(
      "SELECT n FROM NewsArticle n WHERE n.slug = :slug AND n.deletedAt IS NULL"
          + " AND n.publishedAt <= :now")
  Optional<NewsArticle> findVisibleBySlug(@Param("slug") String slug, @Param("now") Instant now);

  /**
   * Si el slug ya lo usa otra noticia, aunque esté borrada (un enlace antiguo no cambia de
   * destino).
   */
  boolean existsBySlugAndIdNot(String slug, Long id);

  boolean existsBySlug(String slug);

  /**
   * Noticias sin borrar con los filtros que lleguen ({@code null} es «sin filtro»). {@code status}:
   * {@code published} (se ven en la web: publicadas como muy tarde en {@code now}), {@code draft}
   * (sin fecha de publicación) o {@code all}.
   */
  @Query(
      "SELECT n FROM NewsArticle n WHERE n.deletedAt IS NULL"
          + " AND (:type IS NULL OR n.type = :type)"
          + " AND (:featured IS NULL OR n.featured = :featured)"
          + " AND (:status = 'all'"
          + " OR (:status = 'draft' AND n.publishedAt IS NULL)"
          + " OR (:status = 'published' AND n.publishedAt <= :now))")
  Page<NewsArticle> search(
      @Param("type") NewsType type,
      @Param("featured") Boolean featured,
      @Param("status") String status,
      @Param("now") Instant now,
      Pageable pageable);
}
