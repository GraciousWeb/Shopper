package com.example.podb.Services.ServiceImpl;


import com.example.podb.Config.JwtService;
import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.UserDTO;
import com.example.podb.Enums.Roles;
import com.example.podb.Exception.BadRequestException;
import com.example.podb.Exception.ResourceNotFoundException;
import com.example.podb.Model.LocalUser;
import com.example.podb.Repository.ConfirmationTokenRepository;
import com.example.podb.email.EmailServiceImpl;
import com.example.podb.token.ConfirmationToken;
import com.example.podb.token.Token;
import com.example.podb.Repository.TokenRepository;
import com.example.podb.Repository.UserRepository;
import com.example.podb.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
//import sendinblue.ApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository ConfirmationTokenRepository;
    private final EmailServiceImpl mailService;

    @Value("${token.validity.minutes}")
    private long tokenValidityMinutes;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {


        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists", HttpStatus.BAD_REQUEST);
        }

        // Mapping UserDTO to LocalUser
        LocalUser user = map2Entity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
        ConfirmationToken confirmationToken = new ConfirmationToken();
        user.setConfirmationToken(confirmationToken);
        confirmationToken.setVerificationToken(generateConfirmationToken());
        confirmationToken.setCreatedAt(LocalDateTime.now());
        confirmationToken.setExpiresAt(LocalDateTime.now()
                .plusMinutes(tokenValidityMinutes));
        confirmationToken.setUser(user);
        log.info("registered");

        String content = EmailServiceImpl.TEST_CONTENT + confirmationToken.getVerificationToken();
        mailService.sendEmail(userDTO.getEmail(), EmailServiceImpl.TEST_SUBJECT, content);

        return map2UserDTO(user);
    }

    public Boolean verifyEmail(String verificationToken) {
        ConfirmationToken confirmationToken = ConfirmationTokenRepository.findByVerificationToken(verificationToken).orElseThrow(() ->
                new ResourceNotFoundException("Token is invalid", HttpStatus.NOT_FOUND));


        if (!confirmationToken.isValid() || confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        try {
            LocalUser user = confirmationToken.getUser();
            user.setEnabled(true);
            user.setLocked(false);
            userRepository.save(user);
            confirmationToken.setValid(false);
            ConfirmationTokenRepository.save(confirmationToken);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        log.info("User saved");
        return true;
    }

    @Override
    public UserDTO createAdmin(UserDTO userDTO) throws BadRequestException {
        // Validate input
        if (userDTO == null) {
            throw new BadRequestException("Provided user data is invalid", HttpStatus.BAD_REQUEST);
        }

        // Check if a user with the same email or username already exists
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }


        try {
            LocalUser user = map2Entity(userDTO);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setRole(Roles.ADMIN);
            LocalUser savedAdmin = userRepository.save(user);
            return map2UserDTO(savedAdmin);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the admin user.", e);
        }
    }


    @Override
    public AuthenticationResponse loginUser(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()
                )
        );
        LocalUser users1 = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDto.getEmail()));
        log.info("========> " + loginDto.getEmail());
        String jwtToken = jwtService.generateToken(users1);

        log.info("this is the user token: --------->" + jwtToken);
        revokeAllToken(users1);

        saveUserToken(users1, jwtToken);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    private void revokeAllToken(LocalUser user) {
        List<Token> tokenList = tokenRepository.findAllValidTokenByUser(user.getId());
        log.info(user.getId() + " =====> This is the id of the user");
        if (tokenList.isEmpty()) {
            return;
        }
        for (Token token : tokenList) {
            token.setRevoked(true);
            token.setExpired(true);
            tokenRepository.saveAll(tokenList);
        }
        log.info("============== Saved tokens ===============");
    }


    @Override
//    @Transactional
    public void enableUser(String verificationToken) {

        ConfirmationToken token = ConfirmationTokenRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        LocalUser user = token.getUser();

        user.setEnabled(true);
        user.setLocked(false);

        token.setValid(false);

        userRepository.save(user);
        log.info("user saved");
    }

    @Override
    public boolean isTokenValid(String verificationToken) {
        Optional<ConfirmationToken> confirmationTokenOpt = ConfirmationTokenRepository.findByVerificationToken(verificationToken);
        if (confirmationTokenOpt.isPresent()) {
            return false;
        }

        ConfirmationToken confirmationToken = confirmationTokenOpt.get();
        LocalDateTime now = LocalDateTime.now();
        if (!confirmationToken.isValid() || now.isAfter(confirmationToken.getExpiresAt())) {
            return false;
        }

        confirmationToken.setValid(false);
        ConfirmationTokenRepository.save(confirmationToken);
        log.info("token saved");

        LocalUser user = confirmationToken.getUser();
        if (user != null) {
            enableUser(user.getEmail());
            return true;
        }

        return false;
    }


    @Override
    public boolean isUserAlreadyVerified(String verificationToken) {
        Optional<ConfirmationToken> confirmationTokenOpt = ConfirmationTokenRepository.findByVerificationToken(verificationToken);

        if (confirmationTokenOpt.isPresent()) {
            LocalUser user = confirmationTokenOpt.get().getUser();
            return user.isEnabled();
        }
        return false;
    }

    @Override
    public String generateConfirmationToken() {
        String alphanumericToken = RandomStringUtils
                .randomAlphanumeric(31);
        return alphanumericToken;

    }


    public static LocalUser map2Entity(UserDTO userDTO) {
        LocalUser user = new LocalUser();
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setRole(Roles.CUSTOMER);

        return user;


    }


    private void saveUserToken(LocalUser savedUser, String jwtToken) {
        Token token = Token.builder()
                .token(jwtToken)
                .users(savedUser)
                .isExpired(false)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);

    }

    public UserDTO map2UserDTO(LocalUser user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;

    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email not found "));

    }
}