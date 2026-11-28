package com.loschicharros.api.people;

import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.people.dto.PersonRequest;
import com.loschicharros.api.people.dto.PersonResponse;
import com.loschicharros.api.security.Roles;
import com.loschicharros.api.storage.ImageUpload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
 * Jugadores y entrenadores (contrato OpenAPI, etiqueta «Personas»). Todo es solo del administrador,
 * también leer: son datos personales, muchos de menores (matriz de permisos). La web pública los ve
 * a través de las plantillas, con el nombre y la inicial del apellido.
 */
@Tag(name = "Personas")
@RestController
@RequestMapping("/api/v1/people")
class PersonController {

  private final PersonService service;

  PersonController(PersonService service) {
    this.service = service;
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Listar personas (panel)")
  @GetMapping
  PageResponse<PersonResponse> search(
      @Parameter(description = "Parte del nombre o los apellidos")
          @RequestParam(required = false)
          @Size(max = 100)
          String search,
      @ParameterObject Pageable pageable) {
    return service.search(search, pageable);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Obtener una persona")
  @GetMapping("/{id}")
  PersonResponse findById(@PathVariable Long id) {
    return service.findById(id);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear una persona")
  @PostMapping
  ResponseEntity<PersonResponse> create(@Valid @RequestBody PersonRequest request) {
    var created = service.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .replaceQuery(null)
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Sustituir una persona")
  @PutMapping("/{id}")
  PersonResponse update(@PathVariable Long id, @Valid @RequestBody PersonRequest request) {
    return service.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Borrar una persona",
      description = "Borrado lógico; se conservan sus pertenencias y resultados.")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Subir una imagen no sigue la tabla de la convención: 413 si pasa de 5 MB, nunca 409
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Subir la foto de una persona",
      description = "Solo se muestra en la web pública si tiene consentimiento de imagen.")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
  @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
  @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
  @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
  @ApiResponse(responseCode = "413", ref = "#/components/responses/PayloadTooLarge")
  @PutMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  PersonResponse upload(
      @PathVariable Long id,
      @Parameter(description = "JPG, PNG o WebP de 5 MB como máximo")
          @RequestPart(name = ImageUpload.FIELD, required = false)
          MultipartFile file) {
    return service.replacePhoto(id, ImageUpload.of(file));
  }
}
