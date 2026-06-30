package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.repo.PasswordResetTokenRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordResetTokenService {

    private final PasswordResetTokenRepo passwordResetTokenRepo;



}
