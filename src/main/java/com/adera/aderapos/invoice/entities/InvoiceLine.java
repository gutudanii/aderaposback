package com.adera.aderapos.invoice.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invoice_line")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InvoiceLine {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(nullable = false, updatable = false)
    private String productName;

    @Column(nullable = false, updatable = false)
    private Integer quantity;

    @Column(nullable = false, updatable = false)
    private BigDecimal unitPrice;

    @Column(nullable = false, updatable = false)
    private BigDecimal lineTotal;

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
