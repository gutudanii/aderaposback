package com.adera.aderapos.sales.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Entity representing an item in a sale transaction.
 */
@Entity
@Table(name = "sale_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private BigDecimal totalPrice;

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
