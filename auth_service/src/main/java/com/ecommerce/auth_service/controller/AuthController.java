package com.ecommerce.auth_service.controller;


import com.ecommerce.auth_service.dto.*;
import com.ecommerce.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")         // base URL
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {

        AuthResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

        // return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse response = authService.login(request);

     //   return ResponseEntity.ok(response);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("JWT Authentication works!");
    }



    // Without this endpoint, the frontend has to decode the JWT or maintain extra state.
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser() {

        return ResponseEntity.ok(
                authService.getCurrentUser()
        );
    }


    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {

        return ResponseEntity.ok(authService.getAllUsers());
    }


    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {

        authService.deleteUser(id);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body("User deleted successfully");
    }


    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(authService.refreshToken(request));
    }


}
