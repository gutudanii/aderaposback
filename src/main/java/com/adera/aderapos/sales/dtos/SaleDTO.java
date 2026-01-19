package com.adera.aderapos.sales.dtos;

import com.adera.aderapos.sales.entities.enums.PaymentMethod;
import com.adera.aderapos.sales.entities.enums.SaleStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Sale entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleDTO {
    private UUID id;
    private UUID shopId;
    private UUID userId;
    private PaymentMethod paymentMethod;
    private SaleStatus status;
    private BigDecimal totalAmount;
    private Instant createdAt;
    private Instant updatedAt;
    private List<SaleItemDTO> saleItems;
    private String createdBy;
    private String updatedBy;
    private Instant deletedAt;
    private String deletedBy;
}
