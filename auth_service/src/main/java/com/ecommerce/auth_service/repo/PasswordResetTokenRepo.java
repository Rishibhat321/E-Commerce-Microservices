package com.ecommerce.auth_service.repo;

import com.ecommerce.auth_service.entity.PasswordResetToken;
import com.ecommerce.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByResetToken(String token);

    void deleteByUser(User user);

}
