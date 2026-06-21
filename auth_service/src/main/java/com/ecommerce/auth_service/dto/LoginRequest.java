package com.ecommerce.auth_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequest {

    @Email(message = "Invalid email")
    @NotBlank(message = "Email is required")
    private String email;


    @NotBlank(message = "Password is required")
    private String password;

}
