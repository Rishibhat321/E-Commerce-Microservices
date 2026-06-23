package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.*;
import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.exception.EmailAlreadyExistsException;
import com.ecommerce.auth_service.exception.InvalidCredentialsException;
import com.ecommerce.auth_service.exception.InvalidRefreshTokenException;
import com.ecommerce.auth_service.exception.UserNotFoundException;
import com.ecommerce.auth_service.repo.RefreshTokenRepo;
import com.ecommerce.auth_service.repo.UserRepo;
import com.ecommerce.auth_service.util.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor     // Constructor Injection
public class AuthServiceImpl implements AuthService{


    // with the final keyword, dependency is mandatory, without it, it may or may not exist
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepo refreshTokenRepo;



    // after registering, user will get automatically logged in.
    // so creating access & refresh tokens
    @Override
    public AuthResponse register(RegisterRequest request) {

        if(userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CUSTOMER)
                .build();

       User savedUser = userRepo.save(user);

       // creating access token
        String accessToken = jwtService.generateToken(savedUser.getEmail());

        // refresh tokens
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);


        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("User registered successfully")
                .build();

    }



    @Override
    public AuthResponse login(LoginRequest request) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (BadCredentialsException ex) {

            throw new InvalidCredentialsException(
                    "Invalid credentials"
            );
        }

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        // generating access token
        String accessToken = jwtService.generateToken(user.getEmail());

        // after authentication succeeds, create refresh tokens
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message("Login successful")
                .build();
    }


    // Current Logged-In User for frontend
    @Override
    public UserProfileResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole())
                .build();

    }


    @Override
    public List<UserProfileResponse> getAllUsers() {

        return userRepo.findAll()
                .stream()
                .map(user -> UserProfileResponse.builder()
                        .id(user.getId())
                        .fullName(user.getFullName())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .role(user.getRole())
                        .build())
                .toList();

    }


    @Override
    public void deleteUser(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

        userRepo.delete(user);

    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        // Case 1: token is invalid
        RefreshToken refreshToken = refreshTokenRepo.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh Token"));

        // Case 2: refresh token revoked if boolean revoked == true
        if(refreshToken.isRevoked()) {
             throw new InvalidRefreshTokenException("Refresh token revoked");
        }

        // Case 3: token got expired, Expiry Time < Current Time
        if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new InvalidRefreshTokenException("Refresh token expired");
        }

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user.getEmail());

        // set revoked to true, old token can't be used anymore, will create new refres token
        refreshToken.setRevoked(true);

        // save to db
        refreshTokenRepo.save(refreshToken);

        // create new refresh token
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(refreshToken.getUser());




        return AuthResponse.builder()
                .accessToken(accessToken)
          //      .refreshToken(refreshToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .message("Access token refreshed successfully")
                .build();

    }


}
