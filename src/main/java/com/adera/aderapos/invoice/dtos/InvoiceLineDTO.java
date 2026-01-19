package com.adera.aderapos.invoice.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Data Transfer Object for Invoice Line items.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceLineDTO {
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal lineTotal;
    private String createdBy;
    private String updatedBy;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant deletedAt;
    private String deletedBy;
}
