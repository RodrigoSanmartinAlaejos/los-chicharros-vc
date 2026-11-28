package com.loschicharros.api.standings;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface StandingRepository extends JpaRepository<Standing, Long> {

  @EntityGraph(attributePaths = {"team.category"})
  List<Standing> findByCompetitionId(Long competitionId);

  @Modifying(flushAutomatically = true)
  @Query("DELETE FROM Standing s WHERE s.competition.id = :competitionId")
  void deleteByCompetition(@Param("competitionId") Long competitionId);
}
