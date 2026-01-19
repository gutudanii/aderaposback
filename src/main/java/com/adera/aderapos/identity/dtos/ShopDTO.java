package com.adera.aderapos.identity.dtos;

import lombok.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopDTO {
    private String name;
    private UUID merchantId;
}
