package com.loschicharros.api.competitions;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface MatchdayRepository extends JpaRepository<Matchday, Long> {

  List<Matchday> findByCompetitionIdOrderByNumber(Long competitionId);

  boolean existsByCompetitionIdAndNumber(Long competitionId, int number);

  boolean existsByCompetitionIdAndNumberAndIdNot(Long competitionId, int number, Long id);
}
