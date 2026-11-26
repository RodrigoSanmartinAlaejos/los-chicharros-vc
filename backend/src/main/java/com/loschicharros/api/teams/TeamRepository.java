package com.loschicharros.api.teams;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface TeamRepository extends JpaRepository<Team, Long> {

  /** Equipos con los filtros que lleguen, por orden de categoría, primero los del club. */
  @Query(
      "SELECT t FROM Team t JOIN FETCH t.category c"
          + " WHERE (:seasonId IS NULL OR t.season.id = :seasonId)"
          + " AND (:categoryId IS NULL OR c.id = :categoryId)"
          + " AND (:ownClub IS NULL OR t.ownClub = :ownClub)"
          + " ORDER BY c.sortOrder, t.ownClub DESC, t.name")
  List<Team> search(
      @Param("seasonId") Long seasonId,
      @Param("categoryId") Long categoryId,
      @Param("ownClub") Boolean ownClub);

  boolean existsBySeasonIdAndCategoryIdAndName(Long seasonId, Long categoryId, String name);

  boolean existsBySeasonIdAndCategoryIdAndNameAndIdNot(
      Long seasonId, Long categoryId, String name, Long id);
}
