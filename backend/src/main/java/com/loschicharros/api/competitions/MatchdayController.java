package com.loschicharros.api.competitions;

import com.loschicharros.api.competitions.dto.MatchdayRequest;
import com.loschicharros.api.competitions.dto.MatchdayResponse;
import com.loschicharros.api.security.Roles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Jornadas (contrato OpenAPI, etiqueta «Competiciones»). Se leen con su competición, en {@code GET
 * /competitions/{id}/matchdays}; escribir es solo del administrador.
 */
@Tag(name = "Competiciones")
@RestController
@RequestMapping("/api/v1/matchdays")
class MatchdayController {

  private final MatchdayService matchdays;

  MatchdayController(MatchdayService matchdays) {
    this.matchdays = matchdays;
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear una jornada")
  @PostMapping
  ResponseEntity<MatchdayResponse> create(@Valid @RequestBody MatchdayRequest request) {
    var created = matchdays.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Sustituir una jornada")
  @PutMapping("/{id}")
  MatchdayResponse update(@PathVariable Long id, @Valid @RequestBody MatchdayRequest request) {
    return matchdays.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Borrar una jornada",
      description = "409 si tiene registros que dependen de él.")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    matchdays.delete(id);
    return ResponseEntity.noContent().build();
  }
}
