package com.adera.aderapos.audit.services;

import com.adera.aderapos.audit.entities.AuditEvent;
import com.adera.aderapos.audit.entities.enums.*;
import com.adera.aderapos.audit.repositories.AuditEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for logging audit events.
 */
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditEventRepository repository;

    public void log(
            AuditAction action,
            AuditEntityType entityType,
            UUID entityId,
            UUID actorId,
            String actorRole,
            AuditSeverity severity,
            String description
    ) {
        AuditEvent event = AuditEvent.builder()
                .action(action)
                .entityType(entityType)
                .entityId(entityId)
                .actorId(actorId)
                .actorRole(actorRole)
                .severity(severity)
                .description(description)
                .occurredAt(Instant.now())
                .build();

        repository.save(event);
    }
}
