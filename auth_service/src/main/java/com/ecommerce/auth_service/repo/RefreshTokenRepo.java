package com.ecommerce.auth_service.repo;

import com.ecommerce.auth_service.entity.RefreshToken;
import com.ecommerce.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {

    // To find token in DB
    Optional<RefreshToken> findByToken(String token);

    // logout all devices, revoke all tokens
    List<RefreshToken> findByUser(User user);

    List<RefreshToken> findAllByUser(User user);


    // for all logout sessions
    void deleteByUser(User user);

}