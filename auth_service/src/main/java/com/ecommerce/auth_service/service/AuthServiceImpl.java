package com.ecommerce.auth_service.service;

import com.ecommerce.auth_service.dto.*;
import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.Role;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.exception.EmailAlreadyExistsException;
import com.ecommerce.auth_service.exception.InvalidCredentialsException;
import com.ecommerce.auth_service.exception.UserNotFoundException;
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


        return createAuthenticationResponse(savedUser, "User registered successfully");

    }


    private void authenticate(LoginRequest request) {

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
    }


    private User getUserByEmail(String email) {

        return userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found"));

    }


    private AuthResponse createAuthenticationResponse(User user, String message) {

        // generating access token
        String accessToken = jwtService.generateToken(user);

        // after authentication succeeds, create refresh tokens
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .message(message)
                .build();

    }


    @Override
    public AuthResponse login(LoginRequest request) {

        authenticate(request);

        User user = getUserByEmail(request.getEmail());

        // rotate refresh tokens
        // revoke previous tokens for the user, every time there is a new login
        refreshTokenService.revokeAllUserTokens(user);

       return createAuthenticationResponse(user, "Login successful");
    }


    // Current Logged-In User for frontend
    @Override
    public UserProfileResponse getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();

        User user = getUserByEmail(email);

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


        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();

        String accessToken = jwtService.generateToken(user);

        // revoke token
        refreshTokenService.revokeRefreshToken(refreshToken);

        // create new refresh token
     //   RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(refreshToken.getUser());
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);


        return AuthResponse.builder()
                .accessToken(accessToken)
          //      .refreshToken(refreshToken.getToken())
                .refreshToken(newRefreshToken.getToken())
                .message("Access token refreshed successfully")
                .build();

    }


    // logout
    public void logout(LogoutRequest request) {

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        refreshTokenService.revokeRefreshToken(refreshToken);
    }


}
