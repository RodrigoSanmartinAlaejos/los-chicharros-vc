package com.loschicharros.api.venues;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

interface VenueRepository extends JpaRepository<Venue, Long> {

  List<Venue> findAllByOrderByNameAsc();

  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);
}
