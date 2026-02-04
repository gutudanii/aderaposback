package com.adera.aderapos.identity.dtos;

import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantDTO {
    private UUID merchantId;
    private String name;
    private String tin;
    private String email;
}

