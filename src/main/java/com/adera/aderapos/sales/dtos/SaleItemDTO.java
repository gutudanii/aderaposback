package com.adera.aderapos.sales.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Data Transfer Object for SaleItem entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItemDTO {
    private UUID id;
    private UUID productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
