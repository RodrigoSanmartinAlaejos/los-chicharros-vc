package com.loschicharros.api.venues;

import com.loschicharros.api.security.Roles;
import com.loschicharros.api.venues.dto.VenueRequest;
import com.loschicharros.api.venues.dto.VenueResponse;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * CRUD de instalaciones (contrato OpenAPI, etiqueta «Configuración»).
 *
 * <p>Listar es público; crear, editar y borrar es solo del administrador (matriz de permisos).
 */
@RestController
@RequestMapping("/api/v1/venues")
class VenueController {

  private final VenueService service;

  VenueController(VenueService service) {
    this.service = service;
  }

  @GetMapping
  List<VenueResponse> findAll() {
    return service.findAll();
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @PostMapping
  ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
    var created = service.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @PutMapping("/{id}")
  VenueResponse update(@PathVariable Long id, @Valid @RequestBody VenueRequest request) {
    return service.update(id, request);
  }

  @PreAuthorize(Roles.ONLY_ADMIN)
  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
