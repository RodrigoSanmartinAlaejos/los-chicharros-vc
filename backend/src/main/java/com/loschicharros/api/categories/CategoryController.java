package com.loschicharros.api.categories;

import com.loschicharros.api.categories.dto.CategoryRequest;
import com.loschicharros.api.categories.dto.CategoryResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
 * CRUD de categorías (contrato OpenAPI, etiqueta «Configuración»).
 *
 * <p>Listar es público; crear, editar y borrar será solo del administrador cuando llegue el login
 * (sprint 2). Hasta entonces no hay seguridad.
 */
@RestController
@RequestMapping("/api/v1/categories")
class CategoryController {

  private final CategoryService service;

  CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping
  List<CategoryResponse> findAll() {
    return service.findAll();
  }

  @PostMapping
  ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
    var created = service.create(request);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(created.id())
            .toUri();
    return ResponseEntity.created(location).body(created);
  }

  @PutMapping("/{id}")
  CategoryResponse update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
    return service.update(id, request);
  }

  @DeleteMapping("/{id}")
  ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }
}
