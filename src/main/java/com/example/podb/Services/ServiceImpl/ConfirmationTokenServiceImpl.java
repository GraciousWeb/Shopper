package com.example.podb.Services.ServiceImpl;

import com.example.podb.Repository.ConfirmationTokenRepository;
import com.example.podb.Services.ConfirmationTokenService;
import com.example.podb.token.ConfirmationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository ConfirmationTokenRepository;
    public void saveConfirmationToken(ConfirmationToken confirmationToken){
        ConfirmationTokenRepository.save(confirmationToken);
    }
    public Optional<ConfirmationToken> getVerificationToken(String token) {
        return ConfirmationTokenRepository.findByVerificationToken(token);
    }


}
