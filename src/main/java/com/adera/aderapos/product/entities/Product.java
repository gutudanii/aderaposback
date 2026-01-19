package com.adera.aderapos.product.entities;

import com.adera.aderapos.identity.entities.Shop;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing a Product in the system.
 */
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"shop_id", "sku"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sku;

    @Column
    private String description;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
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
