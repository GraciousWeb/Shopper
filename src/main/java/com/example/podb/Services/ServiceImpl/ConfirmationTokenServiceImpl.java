package com.example.podb.Services.ServiceImpl;

import com.example.podb.Repository.passwordTokenRepository;
import com.example.podb.Services.ConfirmationTokenService;
import com.example.podb.token.PasswordToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final passwordTokenRepository passwordTokenRepository;
    public void saveConfirmationToken(PasswordToken confirmationToken){
        passwordTokenRepository.save(confirmationToken);
    }
    public Optional<PasswordToken> getVerificationToken(String token) {
        return passwordTokenRepository.findByVerificationToken(token);
    }


}
