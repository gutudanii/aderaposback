package com.adera.aderapos.identity.entities;

import com.adera.aderapos.identity.entities.enums.ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private ROLE role; // MERCHANT_ADMIN or CASHIER

    @ManyToOne
    @JoinColumn(name = "shop_id")
    private Shop shop;

    private String telegramId; // optional Telegram binding

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
