package com.loschicharros.api.categories.dto;

import com.loschicharros.api.categories.Category;
import com.loschicharros.api.categories.Gender;

/** Categoría tal como la devuelve la API (contrato OpenAPI, {@code CategoryResponse}). */
public record CategoryResponse(Long id, String name, Gender gender, int sortOrder) {

  public static CategoryResponse from(Category category) {
    return new CategoryResponse(
        category.getId(), category.getName(), category.getGender(), category.getSortOrder());
  }
}
