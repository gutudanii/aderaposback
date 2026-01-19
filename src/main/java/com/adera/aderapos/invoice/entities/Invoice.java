package com.adera.aderapos.invoice.entities;

import com.adera.aderapos.identity.entities.Shop;
import com.adera.aderapos.invoice.entities.enums.InvoiceStatus;
import com.adera.aderapos.invoice.entities.enums.InvoiceType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing an Invoice.
 */
@Entity
@Table(name = "invoice", indexes = {
        @Index(columnList = "invoiceNumber", unique = true)
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false, updatable = false)
    private Shop shop;

    @Column(nullable = false, updatable = false)
    private UUID saleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private InvoiceType type;

    @Column(nullable = false, updatable = false)
    private BigDecimal netAmount;

    @Column(nullable = false, updatable = false)
    private BigDecimal vatAmount;

    @Column(nullable = false, updatable = false)
    private BigDecimal surtaxAmount;

    @Column(nullable = false, updatable = false)
    private BigDecimal grossAmount;

    @Column(nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String canonicalJson;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String signature;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String invoiceHash;

    @Column(columnDefinition = "TEXT")
    private String qrPayload;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    private List<InvoiceLine> lines;

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

    /** ONLY status transitions allowed */
    public void markStatus(InvoiceStatus status) {
        this.status = status;
    }

    public void setQrPayload(String qrPayload) {
        this.qrPayload = qrPayload;
    }
}
