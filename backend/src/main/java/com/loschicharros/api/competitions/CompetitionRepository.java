package com.loschicharros.api.competitions;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CompetitionRepository extends JpaRepository<Competition, Long> {

  /** Competiciones de una temporada, de una categoría si llega, por orden de categoría y nombre. */
  @Query(
      "SELECT c FROM Competition c JOIN FETCH c.category cat"
          + " WHERE c.season.id = :seasonId"
          + " AND (:categoryId IS NULL OR cat.id = :categoryId)"
          + " ORDER BY cat.sortOrder, c.name")
  List<Competition> search(@Param("seasonId") Long seasonId, @Param("categoryId") Long categoryId);

  boolean existsBySeasonIdAndCategoryIdAndName(Long seasonId, Long categoryId, String name);

  boolean existsBySeasonIdAndCategoryIdAndNameAndIdNot(
      Long seasonId, Long categoryId, String name, Long id);
}
