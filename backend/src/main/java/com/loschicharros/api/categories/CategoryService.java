package com.loschicharros.api.categories;

import com.loschicharros.api.categories.dto.CategoryRequest;
import com.loschicharros.api.categories.dto.CategoryResponse;
import com.loschicharros.api.common.error.ConflictException;
import com.loschicharros.api.common.error.ResourceNotFoundException;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Alta, edición y borrado de categorías. */
@Service
@Transactional(readOnly = true)
public class CategoryService {

  private static final String RESOURCE = "categoría";

  private final CategoryRepository categories;

  public CategoryService(CategoryRepository categories) {
    this.categories = categories;
  }

  /** Todas las categorías, en el orden en que se muestran en la web. */
  public List<CategoryResponse> findAll() {
    return categories.findAllByOrderBySortOrderAscNameAscGenderAsc().stream()
        .map(CategoryResponse::from)
        .toList();
  }

  /**
   * Crea una categoría.
   *
   * @throws ConflictException si ya hay una con el mismo nombre y sexo
   */
  @Transactional
  public CategoryResponse create(CategoryRequest request) {
    if (categories.existsByNameAndGender(request.name(), request.gender())) {
      throw duplicated();
    }
    var category = new Category(request.name(), request.gender(), request.sortOrder());
    return CategoryResponse.from(categories.save(category));
  }

  /**
   * Edita una categoría.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si otra categoría ya tiene el mismo nombre y sexo
   */
  @Transactional
  public CategoryResponse update(Long id, CategoryRequest request) {
    var category = get(id);
    if (categories.existsByNameAndGenderAndIdNot(request.name(), request.gender(), id)) {
      throw duplicated();
    }
    category.update(request.name(), request.gender(), request.sortOrder());
    return CategoryResponse.from(category);
  }

  /**
   * Borra una categoría.
   *
   * @throws ResourceNotFoundException si no existe
   * @throws ConflictException si tiene equipos
   */
  @Transactional
  public void delete(Long id) {
    var category = get(id);
    try {
      categories.delete(category);
      categories.flush();
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("No se puede borrar la categoría: tiene equipos");
    }
  }

  private Category get(Long id) {
    return categories.findById(id).orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
  }

  private static ConflictException duplicated() {
    return new ConflictException("Ya existe una categoría con ese nombre y sexo");
  }
}
