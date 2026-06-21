package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.AuthResponse;
import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.RegisterRequest;
import com.ecommerce.auth_service.dto.UserProfileResponse;

import java.util.List;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getCurrentUser();

    List<UserProfileResponse> getAllUsers();

    void deleteUser(Long userId);


}
