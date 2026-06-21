package com.ecommerce.auth_service.dto;

import lombok.*;

/* server returns after registration/login */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthResponse {

    private String token;

    private String message;

}
