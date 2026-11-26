package com.loschicharros.api.people;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface PersonRepository extends JpaRepository<Person, Long> {

  /**
   * Personas cuyo nombre, apellidos o nombre completo contienen el texto. La colación de la tabla
   * ({@code utf8mb4_0900_ai_ci}) no distingue mayúsculas ni tildes: «lucia» encuentra a «Lucía».
   */
  @Query(
      "SELECT p FROM Person p WHERE p.firstName LIKE :pattern ESCAPE '!'"
          + " OR p.lastName LIKE :pattern ESCAPE '!'"
          + " OR CONCAT(p.firstName, ' ', p.lastName) LIKE :pattern ESCAPE '!'")
  Page<Person> search(@Param("pattern") String pattern, Pageable pageable);
}
