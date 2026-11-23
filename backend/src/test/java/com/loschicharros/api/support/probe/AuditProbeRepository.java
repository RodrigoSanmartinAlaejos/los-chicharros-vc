package com.loschicharros.api.support.probe;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditProbeRepository extends JpaRepository<AuditProbe, Long> {}
