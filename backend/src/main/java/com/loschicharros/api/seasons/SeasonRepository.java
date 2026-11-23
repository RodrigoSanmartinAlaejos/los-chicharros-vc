package com.loschicharros.api.seasons;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface SeasonRepository extends JpaRepository<Season, Long> {

  List<Season> findAllByOrderByStartDateDesc();

  List<Season> findByActiveTrue();

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
