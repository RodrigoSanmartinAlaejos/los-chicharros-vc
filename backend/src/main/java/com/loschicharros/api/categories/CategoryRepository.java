package com.loschicharros.api.categories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface CategoryRepository extends JpaRepository<Category, Long> {

  List<Category> findAllByOrderBySortOrderAscNameAscGenderAsc();

  boolean existsByNameAndGender(String name, Gender gender);

  boolean existsByNameAndGenderAndIdNot(String name, Gender gender, Long id);
}
