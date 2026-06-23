package com.ecommerce.auth_service.service;


import com.ecommerce.auth_service.dto.LogoutRequest;
import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.exception.InvalidRefreshTokenException;
import com.ecommerce.auth_service.repo.RefreshTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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


    public void revokeAllUserTokens(User user) {

        List<RefreshToken> validTokens = refreshTokenRepo.findAllByUserAndRevokedFalse(user);

        /*
        validTokens.forEach(token ->
                token.setRevoked(true));  */

        for(RefreshToken token : validTokens) {
            token.setRevoked(true);
        }

        refreshTokenRepo.saveAll(validTokens);
    }


    public RefreshToken validateRefreshToken(String token) {

        // Case 1: token is invalid
        RefreshToken refreshToken = refreshTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh Token"));

        // Case 2: refresh token revoked if boolean revoked == true
        if(refreshToken.isRevoked()) {
            throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        // Case 3: token got expired, Expiry Time < Current Time
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        return refreshToken;

    }


    public void revokeToken(RefreshToken refreshToken) {

        // for idempotency
        if(!refreshToken.isRevoked()) {
            // set revoked to true, old token can't be used anymore, will create new refresh token
            refreshToken.setRevoked(true);

            // save to db
            refreshTokenRepo.save(refreshToken);
        }

    }


}
