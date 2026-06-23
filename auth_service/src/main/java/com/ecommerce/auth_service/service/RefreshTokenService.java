package com.ecommerce.auth_service.service;


import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepo refreshTokenRepo;

    public String generateRefreshTokenString() {
        return UUID.randomUUID().toString();
    }

    public RefreshToken createRefreshToken(User user) {

        RefreshToken refreshToken =
                RefreshToken.builder()
                        .token(generateRefreshTokenString())
                        .expiryDate(LocalDateTime.now().plusDays(7))
                        .revoked(false)
                        .user(user)
                        .build();

        return refreshTokenRepo.save(refreshToken);

    }


}
