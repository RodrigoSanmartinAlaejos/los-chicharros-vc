package com.loschicharros.api.competitions;

import com.loschicharros.api.competitions.dto.CompetitionRequest;
import com.loschicharros.api.competitions.dto.CompetitionResponse;
import com.loschicharros.api.security.Roles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Competiciones (contrato OpenAPI, etiqueta «Competiciones»). Leer es público: la web las muestra
 * en el calendario y la clasificación. Escribir es solo del administrador (matriz de permisos). Las
 * jornadas con sus partidos se leen en {@code MatchdayScheduleController}.
 */
@Tag(name = "Competiciones")
@RestController
@RequestMapping("/api/v1/competitions")
class CompetitionController {

  private final CompetitionService competitions;

  CompetitionController(CompetitionService competitions) {
    this.competitions = competitions;
  }

  @Operation(summary = "Listar competiciones")
  @GetMapping
  List<CompetitionResponse> search(
      @Parameter(description = "Por defecto, la temporada activa") @RequestParam(required = false)
          Long seasonId,
      @RequestParam(required = false) Long categoryId) {
    return competitions.search(seasonId, categoryId);
  }

  @Operation(summary = "Obtener una competición")
  @GetMapping("/{id}")
  CompetitionResponse findById(@PathVariable Long id) {
    return competitions.findById(id);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear una competición")
  @PostMapping
  ResponseEntity<CompetitionResponse> create(@Valid @RequestBody CompetitionRequest request) {
    var created = competitions.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Sustituir una competición")
  @PutMapping("/{id}")
  CompetitionResponse update(
      @PathVariable Long id, @Valid @RequestBody CompetitionRequest request) {
    return competitions.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Borrar una competición",
      description = "409 si tiene registros que dependen de él.")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    competitions.delete(id);
    return ResponseEntity.noContent().build();
  }
}
