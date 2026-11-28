package com.loschicharros.api.teams;

import com.loschicharros.api.security.Roles;
import com.loschicharros.api.storage.ImageUpload;
import com.loschicharros.api.teams.dto.MembershipPatchRequest;
import com.loschicharros.api.teams.dto.MembershipRequest;
import com.loschicharros.api.teams.dto.MembershipResponse;
import com.loschicharros.api.teams.dto.TeamRequest;
import com.loschicharros.api.teams.dto.TeamResponse;
import com.loschicharros.api.teams.dto.TeamSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Equipos y plantillas (contrato OpenAPI, etiqueta «Equipos»). Leer es público: la web muestra los
 * equipos y sus plantillas con los datos públicos de cada persona. Escribir es solo del
 * administrador (matriz de permisos).
 */
@Tag(name = "Equipos")
@RestController
@RequestMapping("/api/v1/teams")
class TeamController {

  private static final String BAD_REQUEST = "#/components/responses/BadRequest";
  private static final String UNAUTHORIZED = "#/components/responses/Unauthorized";
  private static final String FORBIDDEN = "#/components/responses/Forbidden";
  private static final String NOT_FOUND = "#/components/responses/NotFound";
  private static final String CONFLICT = "#/components/responses/Conflict";

  private final TeamService teams;
  private final RosterService roster;

  TeamController(TeamService teams, RosterService roster) {
    this.teams = teams;
    this.roster = roster;
  }

  @Operation(summary = "Listar equipos")
  @GetMapping
  List<TeamSummary> search(
      @RequestParam(required = false) Long seasonId,
      @RequestParam(required = false) Long categoryId,
      @RequestParam(required = false) Boolean ownClub) {
    return teams.search(seasonId, categoryId, ownClub);
  }

  @Operation(
      summary = "Obtener un equipo",
      description =
          "Incluye la plantilla pública: nombre e inicial del apellido, dorsal, posición y foto"
              + " solo con consentimiento.")
  @GetMapping("/{id}")
  TeamResponse findById(@PathVariable Long id) {
    return teams.findById(id);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Crear un equipo")
  @PostMapping
  ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
    var created = teams.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Sustituir un equipo")
  @PutMapping("/{id}")
  TeamResponse update(@PathVariable Long id, @Valid @RequestBody TeamRequest request) {
    return teams.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Borrar un equipo",
      description = "409 si tiene registros que dependen de él.")
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    teams.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(
      summary = "Añadir una persona a la plantilla",
      description = "409 si la persona ya está con ese tipo o el dorsal está ocupado.")
  @PostMapping("/{id}/roster")
  ResponseEntity<MembershipResponse> addToRoster(
      @PathVariable Long id, @Valid @RequestBody MembershipRequest request) {
    var created = roster.add(id, request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{membershipId}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  // PATCH y el DELETE de la plantilla no siguen la tabla de la convención: el cambio de dorsal
  // puede dar 409 y quitar a alguien nunca lo da
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Cambiar dorsal o posición")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = BAD_REQUEST)
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @ApiResponse(responseCode = "409", ref = CONFLICT)
  @PatchMapping("/{id}/roster/{membershipId}")
  MembershipResponse changeInRoster(
      @PathVariable Long id,
      @PathVariable Long membershipId,
      @Valid @RequestBody MembershipPatchRequest request) {
    return roster.change(id, membershipId, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Quitar de la plantilla")
  @ApiResponse(responseCode = "204", description = "Sin contenido")
  @ApiResponse(responseCode = "401", ref = UNAUTHORIZED)
  @ApiResponse(responseCode = "403", ref = FORBIDDEN)
  @ApiResponse(responseCode = "404", ref = NOT_FOUND)
  @DeleteMapping("/{id}/roster/{membershipId}")
  ResponseEntity<Void> removeFromRoster(@PathVariable Long id, @PathVariable Long membershipId) {
    roster.remove(id, membershipId);
    return ResponseEntity.noContent().build();
  }

  // Subir una imagen no sigue la tabla de la convención: 413 si pasa de 5 MB, nunca 409
  @PreAuthorize(Roles.ONLY_ADMIN)
  @Operation(summary = "Subir el escudo o la foto de un equipo")
  @ApiResponse(responseCode = "200", description = "OK")
  @ApiResponse(responseCode = "400", ref = "#/components/responses/BadRequest")
  @ApiResponse(responseCode = "401", ref = "#/components/responses/Unauthorized")
  @ApiResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
  @ApiResponse(responseCode = "404", ref = "#/components/responses/NotFound")
  @ApiResponse(responseCode = "413", ref = "#/components/responses/PayloadTooLarge")
  @PutMapping(value = "/{id}/crest", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  TeamResponse upload(
      @PathVariable Long id,
      @Parameter(description = "JPG, PNG o WebP de 5 MB como máximo")
          @RequestPart(name = ImageUpload.FIELD, required = false)
          MultipartFile file) {
    return teams.replaceCrest(id, ImageUpload.of(file));
  }
}
