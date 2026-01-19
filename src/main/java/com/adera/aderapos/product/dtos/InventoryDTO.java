package com.adera.aderapos.product.dtos;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Data Transfer Object for Inventory entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryDTO {

    private UUID id;
    private UUID productId;
    private Integer quantity;
    private Integer reserved;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;
    private Instant deletedAt;
    private String deletedBy;
}
