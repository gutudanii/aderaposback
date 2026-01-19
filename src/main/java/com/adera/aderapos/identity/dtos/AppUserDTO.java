package com.adera.aderapos.identity.dtos;

import lombok.*;
import com.adera.aderapos.identity.entities.enums.ROLE;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppUserDTO {
    private String username;
    private String password;
    private ROLE role;
    private UUID shopId;
    private String telegramId;
}
