package com.loschicharros.api.news;

import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.news.dto.NewsPatchRequest;
import com.loschicharros.api.news.dto.NewsRequest;
import com.loschicharros.api.news.dto.NewsResponse;
import com.loschicharros.api.news.dto.NewsSummary;
import com.loschicharros.api.security.Roles;
import com.loschicharros.api.storage.ImageUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Noticias (contrato OpenAPI, etiqueta «Noticias»). La web lee las publicadas sin sesión (el
 * listado y el detalle por slug); los borradores, el detalle por id y escribir son solo del
 * administrador (matriz de permisos).
 */
@Tag(name = "Noticias")
@RestController
@RequestMapping("/api/v1/news")
class NewsController {

  private static final String BAD_REQUEST = "#/components/responses/BadRequest";
  private static final String UNAUTHORIZED = "#/components/responses/Unauthorized";
  private static final String FORBIDDEN = "#/components/responses/Forbidden";
  private static final String NOT_FOUND = "#/components/responses/NotFound";
  private static final String ADMIN_AUTHORITY = "ROLE_" + Roles.ADMIN;

  private final NewsService news;

  NewsController(NewsService news) {
    this.news = news;
  }

  // Público, pero los borradores piden sesión de administrador: 403 sin ella. El token es opcional
  @Operation(
      summary = "Listar noticias",
      security = {@SecurityRequirement(name = "bearerAuth")})
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @GetMapping
  PageResponse<NewsSummary> search(
      @RequestParam(required = false) NewsType type,
      @RequestParam(required = false) Boolean featured,
      @Parameter(
              description =
                  "`draft` y `all` (borradores y publicadas, también las programadas) son solo del"
                      + " administrador",
              schema =
                  @Schema(
                      allowableValues = {"published", "draft", "all"},
                      defaultValue = "published"))
          @RequestParam(defaultValue = "published")
          String status,
      @ParameterObject Pageable pageable,
      Authentication authentication) {
    return news.search(type, featured, status, isAdmin(authentication), pageable);
  }

  @Operation(summary = "Obtener una noticia publicada por su slug")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @GetMapping("/by-slug/{slug}")
  NewsResponse findBySlug(@PathVariable @Size(max = 170) String slug) {
    return news.findPublishedBySlug(slug);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Obtener una noticia por id (panel)")
  @GetMapping("/{id}")
  NewsResponse findById(@PathVariable Long id) {
    return news.findById(id);
  }

  // El slug se genera sin repetirse, así que crear o sustituir nunca da 409
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear una noticia", description = "Se crea como borrador.")
  @ApiResponse(
      responseCode = "201",
      description = "Creado",
      headers = @Header(name = "Location", description = "URL del recurso creado"))
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @PostMapping
  ResponseEntity<NewsResponse> create(@Valid @RequestBody NewsRequest request) {
    var created = news.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQuery(null)
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Sustituir una noticia",
      description =
          "Mientras es un borrador, el slug cambia con el título; publicada, se conserva.")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @PutMapping("/{id}")
  NewsResponse update(@PathVariable Long id, @Valid @RequestBody NewsRequest request) {
    return news.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Publicar, retirar o destacar")
  @PatchMapping("/{id}")
  NewsResponse change(@PathVariable Long id, @RequestBody NewsPatchRequest request) {
    return news.change(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Borrar una noticia", description = "Borrado lógico.")
  @ApiResponse(responseCode = "204", description = "Sin contenido")
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    news.delete(id);
    return ResponseEntity.noContent().build();
  }

  private static boolean isAdmin(Authentication authentication) {
    return authentication != null
        && authentication.getAuthorities().stream()
            .anyMatch(authority -> ADMIN_AUTHORITY.equals(authority.getAuthority()));
  }

  // Subir una imagen no sigue la tabla de la convención: 413 si pasa de 5 MB, nunca 409
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Subir la imagen de una noticia")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
  @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
  @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
  @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
  @ApiResponse(responseCode = "413", ref = "#/components/responses/PayloadTooLarge")
  @PutMapping(value = "/{id}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  NewsResponse upload(
      @PathVariable Long id,
      @Parameter(description = "JPG, PNG o WebP de 5 MB como máximo")
          @RequestPart(name = ImageUpload.FIELD, required = false)
          MultipartFile file) {
    return news.replaceImage(id, ImageUpload.of(file));
  }
}
