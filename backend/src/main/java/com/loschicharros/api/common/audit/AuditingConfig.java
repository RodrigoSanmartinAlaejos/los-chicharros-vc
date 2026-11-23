package com.loschicharros.api.common.audit;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/** Activa la auditoría de Spring Data: fechas y usuario de creación y de modificación. */
@Configuration(proxyBeanMethods = false)
@EnableJpaAuditing(auditorAwareRef = "auditorAware", dateTimeProviderRef = "auditDateTimeProvider")
class AuditingConfig {

  @Bean
  AuditorAware<Long> auditorAware(CurrentUser currentUser) {
    return currentUser::id;
  }

  /**
   * Hora actual en microsegundos, la precisión de {@code DATETIME(6)}: así la entidad recién
   * guardada devuelve la misma fecha que se leerá después de la base.
   */
  @Bean
  DateTimeProvider auditDateTimeProvider() {
    return () -> Optional.of(Instant.now().truncatedTo(ChronoUnit.MICROS));
  }
}
