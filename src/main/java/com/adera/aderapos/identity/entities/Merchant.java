package com.adera.aderapos.identity.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Merchant {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String tin; // Tax Identification Number

    private String email;

    @OneToMany(mappedBy = "merchant", cascade = CascadeType.ALL)
    private List<Shop> shops;

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
