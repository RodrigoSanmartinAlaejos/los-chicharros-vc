package com.loschicharros.api.support.probe;

import java.time.Instant;

public record ProbeResponse(
    Long id, String name, Instant createdAt, Long createdBy, Instant updatedAt, Long updatedBy) {

  static ProbeResponse of(AuditProbe probe) {
    return new ProbeResponse(
        probe.getId(),
        probe.getName(),
        probe.getCreatedAt(),
        probe.getCreatedBy(),
        probe.getUpdatedAt(),
        probe.getUpdatedBy());
  }
}
