package com.adera.aderapos.audit.repositories;

import com.adera.aderapos.audit.entities.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository interface for AuditEvent entity.
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {
}
