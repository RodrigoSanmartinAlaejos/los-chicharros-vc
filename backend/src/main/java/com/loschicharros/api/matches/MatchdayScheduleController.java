package com.loschicharros.api.matches;

import com.loschicharros.api.matches.dto.MatchdayWithMatches;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Jornadas de una competición con sus partidos (contrato OpenAPI, etiqueta «Competiciones»). Está
 * aquí y no en {@code CompetitionController} porque necesita los partidos, y el paquete de
 * competiciones no depende del de partidos. Es público.
 */
@Tag(name = "Competiciones")
@RestController
class MatchdayScheduleController {

  private final MatchService matches;

  MatchdayScheduleController(MatchService matches) {
    this.matches = matches;
  }

  @Operation(summary = "Jornadas con sus partidos")
  @GetMapping("/api/v1/competitions/{id}/matchdays")
  List<MatchdayWithMatches> matchdays(@PathVariable Long id) {
    return matches.matchdaysOf(id);
  }
}
