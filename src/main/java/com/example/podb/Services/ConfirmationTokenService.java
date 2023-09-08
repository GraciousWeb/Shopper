package com.example.podb.Services;

import com.example.podb.token.PasswordToken;

import java.util.Optional;


public interface ConfirmationTokenService  {
    void saveConfirmationToken(PasswordToken token);
    Optional<PasswordToken> getVerificationToken(String verificationToken);
//    ConfirmationToken setConfirmedAt(String verificationToken);
//    LocalDateTime getConfirmedAt(String verificationToken);
}
