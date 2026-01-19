package com.adera.aderapos.sales.entities;

import com.adera.aderapos.identity.entities.AppUser;
import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.sales.entities.enums.PaymentMethod;
import com.adera.aderapos.sales.entities.enums.SaleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Sale entity representing a sales transaction in the POS system.
 */
@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleItem> saleItems;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

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
