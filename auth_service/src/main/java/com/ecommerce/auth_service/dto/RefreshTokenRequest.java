package com.ecommerce.auth_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.*;


// used by the frontend call


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

}
