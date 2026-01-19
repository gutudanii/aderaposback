package com.adera.aderapos.product.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Product entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {

    private UUID id;
    private UUID shopId;
    private String name;
    private String sku;
    private String description;
    private BigDecimal unitPrice;
    private Boolean active;
    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Instant deletedAt;
    private String deletedBy;
}
