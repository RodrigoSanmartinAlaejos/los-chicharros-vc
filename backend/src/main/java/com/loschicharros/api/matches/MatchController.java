package com.loschicharros.api.matches;

import com.loschicharros.api.common.web.PageResponse;
import com.loschicharros.api.matches.dto.MatchPatchRequest;
import com.loschicharros.api.matches.dto.MatchRequest;
import com.loschicharros.api.matches.dto.MatchResponse;
import com.loschicharros.api.security.Roles;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Partidos (contrato OpenAPI, etiqueta «Partidos»). Leer es público: es el calendario de la web.
 * Escribir es solo del administrador (matriz de permisos).
 */
@Tag(name = "Partidos")
@RestController
@RequestMapping("/api/v1/matches")
class MatchController {

  private static final String BAD_REQUEST = "#/components/responses/BadRequest";
  private static final String UNAUTHORIZED = "#/components/responses/Unauthorized";
  private static final String FORBIDDEN = "#/components/responses/Forbidden";
  private static final String NOT_FOUND = "#/components/responses/NotFound";

  private final MatchService matches;

  MatchController(MatchService matches) {
    this.matches = matches;
  }

  @Operation(summary = "Listar partidos")
  @GetMapping
  PageResponse<MatchResponse> search(
      @RequestParam(required = false) Long teamId,
      @RequestParam(required = false) Long venueId,
      @Parameter(description = "Desde (incluido), ISO 8601")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate from,
      @Parameter(description = "Hasta (incluido), ISO 8601")
          @RequestParam(required = false)
          @DateTimeFormat(iso = ISO.DATE)
          LocalDate to,
      @RequestParam(required = false) MatchStatus status,
      @ParameterObject Pageable pageable) {
    return matches.search(teamId, venueId, from, to, status, pageable);
  }

  @Operation(summary = "Obtener un partido")
  @GetMapping("/{id}")
  MatchResponse findById(@PathVariable Long id) {
    return matches.findById(id);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear un partido")
  @PostMapping
  ResponseEntity<MatchResponse> create(@Valid @RequestBody MatchRequest request) {
    var created = matches.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Sustituir un partido")
  @PutMapping("/{id}")
  MatchResponse update(@PathVariable Long id, @Valid @RequestBody MatchRequest request) {
    return matches.update(id, request);
  }

  // PATCH no sigue la tabla de la convención: cambiar el estado nunca da 409
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Cambiar estado o marcador de sets",
      description =
          "Para aplazar, cancelar o registrar el resultado (pasa a `FINISHED`). Uno de los dos"
              + " tiene que llegar a 3 sets y el otro a menos de 3.")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @PatchMapping("/{id}")
  MatchResponse change(@PathVariable Long id, @RequestBody MatchPatchRequest request) {
    return matches.change(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Borrar un partido",
      description = "409 si tiene registros que dependen de él.")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    matches.delete(id);
    return ResponseEntity.noContent().build();
  }
}
