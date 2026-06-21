package com.ecommerce.auth_service.dto;

import com.ecommerce.auth_service.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private Role role;

}
