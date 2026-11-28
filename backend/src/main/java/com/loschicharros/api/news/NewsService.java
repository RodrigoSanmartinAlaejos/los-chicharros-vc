package com.loschicharros.api.news;

import com.loschicharros.api.common.error.InvalidFieldException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.news.dto.NewsPatchRequest;
import com.loschicharros.api.news.dto.NewsRequest;
import com.loschicharros.api.news.dto.NewsResponse;
import com.loschicharros.api.news.dto.NewsSummary;
import com.loschicharros.api.storage.ImageUpload;
import com.loschicharros.api.storage.StoredImages;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Noticias (H12): alta y edición en Markdown, publicar y retirar, destacar y borrado lógico, y su
 * lectura en la web. Una noticia se ve en la web si tiene fecha de publicación y esa fecha ya ha
 * llegado (diagrama «Estados de una noticia»).
 */
@Service
@Transactional(readOnly = true)
public class NewsService {

  private static final String RESOURCE = "noticia";

  /** Estados del filtro {@code status}; los que no son {@code published} son solo del panel. */
  private static final Set<String> STATUSES = Set.of("published", "draft", "all");

  /** Campos por los que se puede ordenar el listado. */
  private static final Set<String> SORTABLE = Set.of("publishedAt", "createdAt", "title");

  private final NewsRepository news;
  private final MarkdownRenderer markdown;
  private final StoredImages images;
  private final Clock clock;

  @Autowired
  public NewsService(NewsRepository news, MarkdownRenderer markdown, StoredImages images) {
    this(news, markdown, images, Clock.systemUTC());
  }

  NewsService(NewsRepository news, MarkdownRenderer markdown, StoredImages images, Clock clock) {
    this.news = news;
    this.markdown = markdown;
    this.images = images;
    this.clock = clock;
  }

  /**
   * Página de noticias. Sin sesión de administrador solo se pueden pedir las publicadas, que por
   * defecto van de la más reciente a la más antigua; los borradores, por la última edición, y todas
   * juntas, por la fecha de alta.
   *
   * @throws InvalidFieldException si el estado no existe o se pide ordenar por otro campo
   * @throws AccessDeniedException si pide borradores o todas sin ser administrador
   */
  public PageResponse<NewsSummary> search(
      NewsType type, Boolean featured, String status, boolean admin, Pageable pageable) {
    if (!STATUSES.contains(status)) {
      throw new InvalidFieldException("status", "Tiene que ser published, draft o all");
    }
    if (!admin && !"published".equals(status)) {
      throw new AccessDeniedException("Los borradores solo los ve el administrador");
    }
    var page =
        news.search(type, featured, status, now(), withDefaultSort(pageable, defaultSort(status)));
    return PageResponse.from(page, NewsSummary::from);
  }

  /**
   * Noticia publicada por su slug, con el cuerpo en HTML saneado, para la web.
   *
   * @throws ResourceNotFoundException si no existe, es un borrador, está programada o borrada
   */
  public NewsResponse findPublishedBySlug(String slug) {
    var article =
        news.findVisibleBySlug(slug, now())
            .orElseThrow(() -> new ResourceNotFoundException("No existe la noticia " + slug));
    return NewsResponse.published(article, markdown.toSafeHtml(article.getContent()));
  }

  /**
   * Noticia por su id para el panel, también si es un borrador, con su Markdown.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  public NewsResponse findById(Long id) {
    return forEditor(get(id));
  }

  /** Crea la noticia como borrador, con un slug a partir del título que no usa ninguna otra. */
  @Transactional
  public NewsResponse create(NewsRequest request) {
    var title = request.title().strip();
    var article =
        new NewsArticle(
            title,
            freeSlug(title, null),
            request.summary().strip(),
            request.content(),
            request.type(),
            request.isFeatured());
    return forEditor(news.save(article));
  }

  /**
   * Edita la noticia. Mientras es un borrador, el slug sigue al título; una vez publicada se
   * conserva, para no romper los enlaces que ya se hayan compartido.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  @Transactional
  public NewsResponse update(Long id, NewsRequest request) {
    var article = get(id);
    var title = request.title().strip();
    if (article.isDraft() && !title.equals(article.getTitle())) {
      article.changeSlug(freeSlug(title, id));
    }
    article.update(
        title, request.summary().strip(), request.content(), request.type(), request.isFeatured());
    return forEditor(article);
  }

  /**
   * Publica (con la fecha que llegue; si es futura, se verá en la web desde entonces), retira
   * ({@code publishedAt: null}) o destaca la noticia. Lo que no llega no cambia.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  @Transactional
  public NewsResponse change(Long id, NewsPatchRequest request) {
    var article = get(id);
    if (request.hasPublishedAt()) {
      var when = request.getPublishedAt();
      article.publishAt(when == null ? null : when.truncatedTo(ChronoUnit.MICROS));
    }
    if (request.getFeatured() != null) {
      article.markFeatured(request.getFeatured());
    }
    return forEditor(article);
  }

  /**
   * Pone o cambia la imagen principal (H14); la anterior se borra al confirmar el cambio.
   *
   * @throws ResourceNotFoundException si no existe o está borrada
   */
  @Transactional
  public NewsResponse replaceImage(Long id, ImageUpload upload) {
    var article = get(id);
    article.changeImage(images.replace("news", "noticia-" + id, upload, article.getImageUrl()));
    return forEditor(article);
  }

  /**
   * Borrado lógico: la noticia desaparece de la web y del panel, pero su slug no se vuelve a usar.
   *
   * @throws ResourceNotFoundException si no existe o ya está borrada
   */
  @Transactional
  public void delete(Long id) {
    get(id).markDeleted(now());
  }

  private NewsArticle get(Long id) {
    return news.findByIdAndDeletedAtIsNull(id)
        .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private NewsResponse forEditor(NewsArticle article) {
    return NewsResponse.forEditor(article, markdown.toSafeHtml(article.getContent()));
  }

  /** El slug del título, o con «-2», «-3»… si ya lo tiene otra noticia (también una borrada). */
  private String freeSlug(String title, Long ownId) {
    var base = Slugs.from(title);
    var candidate = base;
    for (var n = 2; taken(candidate, ownId); n++) {
      candidate = base + "-" + n;
    }
    return candidate;
  }

  private boolean taken(String slug, Long ownId) {
    return ownId == null ? news.existsBySlug(slug) : news.existsBySlugAndIdNot(slug, ownId);
  }

  private Instant now() {
    return clock.instant().truncatedTo(ChronoUnit.MICROS);
  }

  private static Sort defaultSort(String status) {
    return switch (status) {
      case "published" -> Sort.by(Sort.Direction.DESC, "publishedAt", "id");
      case "draft" -> Sort.by(Sort.Direction.DESC, "updatedAt", "id");
      default -> Sort.by(Sort.Direction.DESC, "createdAt", "id");
    };
  }

  /** Añade el orden por defecto como desempate y rechaza los campos que no se pueden ordenar. */
  private static Pageable withDefaultSort(Pageable pageable, Sort defaultSort) {
    for (var order : pageable.getSort()) {
      if (!SORTABLE.contains(order.getProperty())) {
        throw new InvalidFieldException("sort", "No se puede ordenar por " + order.getProperty());
      }
    }
    return PageRequest.of(
        pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort().and(defaultSort));
  }
}
