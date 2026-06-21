 package com.ecommerce.auth_service.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Cannot contain null values in DB
    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String password;


    // adding constraints
    // preventing duplicates, null values
    @Column(nullable = false, unique = true)
    private String email;

    private String phoneNumber;


    // Instead of storing numbers (ordinal values), DB will store strings
    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


    // Jpa lifecycle callback
    // before INSERT query executes
    // ADVANTAGE - we do not have to manually set timestamp every time
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }


    // before UPDATE query executes
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }



}
