package com.example.podb.Repository;

import com.example.podb.token.PasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface passwordTokenRepository extends JpaRepository<PasswordToken, Long> {
    Optional<PasswordToken> findByVerificationToken (String VerificationToken);
}
