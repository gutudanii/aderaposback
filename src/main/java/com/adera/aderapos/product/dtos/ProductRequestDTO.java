package com.adera.aderapos.product.dtos;

import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {
    private UUID shopId;
    private String name;
    private String sku;
    private String description;
    private BigDecimal unitPrice;
    private Boolean active;
    private Integer initialQuantity; // Optional: initial inventory quantity
}
