package com.adera.aderapos.audit.entities;

import com.adera.aderapos.audit.entities.enums.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events", indexes = {
        @Index(name = "idx_audit_entity", columnList = "entityType,entityId"),
        @Index(name = "idx_audit_actor", columnList = "actorId"),
        @Index(name = "idx_audit_time", columnList = "occurredAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditEvent {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditEntityType entityType;

    @Column(nullable = false)
    private UUID entityId;

    private UUID actorId;

    @Column(nullable = false)
    private String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuditSeverity severity;

    @Column(nullable = false)
    private Instant occurredAt;

    @Column(length = 1024)
    private String description;
}
