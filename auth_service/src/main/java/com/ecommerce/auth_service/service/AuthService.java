package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.*;

import java.util.List;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    UserProfileResponse getCurrentUser();

    List<UserProfileResponse> getAllUsers();

    void deleteUser(Long userId);

    AuthResponse refreshToken(RefreshTokenRequest request);



}
