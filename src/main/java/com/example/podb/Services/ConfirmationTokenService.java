package com.example.podb.Services;

import com.example.podb.token.ConfirmationToken;

import java.util.Optional;


public interface ConfirmationTokenService  {
    void saveConfirmationToken(ConfirmationToken token);
    Optional<ConfirmationToken> getVerificationToken(String verificationToken);
//    ConfirmationToken setConfirmedAt(String verificationToken);
//    LocalDateTime getConfirmedAt(String verificationToken);
}
