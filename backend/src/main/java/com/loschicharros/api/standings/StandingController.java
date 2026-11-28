package com.loschicharros.api.standings;

import com.loschicharros.api.security.Roles;
import com.loschicharros.api.standings.dto.StandingRequest;
import com.loschicharros.api.standings.dto.StandingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Clasificación de una competición (contrato OpenAPI, etiqueta «Competiciones»). Leer es público;
 * sustituirla a mano es solo del administrador y solo existe en la fase 1.
 */
@Tag(name = "Competiciones")
@RestController
@RequestMapping("/api/v1/competitions/{id}/standings")
class StandingController {

  private static final String BAD_REQUEST = "#/components/responses/BadRequest";
  private static final String UNAUTHORIZED = "#/components/responses/Unauthorized";
  private static final String FORBIDDEN = "#/components/responses/Forbidden";
  private static final String NOT_FOUND = "#/components/responses/NotFound";

  private final StandingService standings;

  StandingController(StandingService standings) {
    this.standings = standings;
  }

  @Operation(
      summary = "Clasificación de una competición",
      description = "Ordenada por puntos, partidos ganados, cociente de sets y cociente de puntos.")
  @GetMapping
  List<StandingResponse> findByCompetition(@PathVariable Long id) {
    return standings.findByCompetition(id);
  }

  // PUT de una lista no sigue la tabla de la convención: sustituirla nunca da 409. Cada fila se
  // valida en el servicio para que el error diga la fila ([i].campo).
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Sustituir la clasificación a mano",
      description = "Solo en la fase 1; desaparece en la fase 3, cuando se calcula sola.")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @PutMapping
  List<StandingResponse> replace(
      @PathVariable Long id, @RequestBody List<StandingRequest> request) {
    return standings.replace(id, request);
  }
}
