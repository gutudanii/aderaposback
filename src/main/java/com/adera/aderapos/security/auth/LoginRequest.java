package com.adera.aderapos.security.auth;

import lombok.*;

/**
 * Data Transfer Object for login requests.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
    private String username;
    private String password;
}
