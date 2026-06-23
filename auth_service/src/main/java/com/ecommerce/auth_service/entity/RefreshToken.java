package com.ecommerce.auth_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // every token must be unique
    @Column(nullable = false, unique = true)
    private String token;

    // logout and token revocation
    private boolean revoked;

    @Column(nullable = false)
    private LocalDateTime expiryDate;


    // many refresh tokens can belong to one user
    // By default ManyToOne is eager, we have defined LAZY Explicitly
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}