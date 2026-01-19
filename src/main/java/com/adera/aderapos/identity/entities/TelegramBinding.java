package com.adera.aderapos.identity.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TelegramBinding {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String telegramUserId;

    @ManyToOne
    @JoinColumn(name = "app_user_id")
    private AppUser appUser;

    private boolean verified;

    // Audit fields
    @Column(updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;
    private String updatedBy;

    private Instant deletedAt;
    private String deletedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
