package com.adera.aderapos.identity.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MerchantDTO {
    private String name;
    private String tin;
    private String email;
}

