package com.example.podb.Services.ServiceImpl;


import com.example.podb.Config.JwtService;
import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.SignUpDto;
import com.example.podb.Enums.Roles;
import com.example.podb.Exception.BadRequestException;
import com.example.podb.Exception.ResourceNotFoundException;
import com.example.podb.Model.LocalUser;
import com.example.podb.Repository.passwordTokenRepository;
import com.example.podb.Services.ConfirmationTokenService;
import com.example.podb.email.EmailSender;
import com.example.podb.token.PasswordToken;
import com.example.podb.token.Token;
import com.example.podb.Repository.TokenRepository;
import com.example.podb.Repository.UserRepository;
import com.example.podb.Services.UserServices;
import com.example.podb.emailService.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
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
public class UserServiceImpl implements UserServices, UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailValidator emailValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final passwordTokenRepository passwordTokenRepository;
    private final JavaMailSender javaMailSender;

    private final EmailSender emailSender;

    @Value("${token.validity.minutes}")
    private long tokenValidityMinutes;

    @Override
    @Transactional
    public String registerUser(SignUpDto signUpDto) {
        boolean isValidEmail = emailValidator.test(signUpDto.getEmail());

        if (!isValidEmail) {
            throw new BadRequestException("Email is invalid", HttpStatus.BAD_REQUEST);
        }

        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new BadRequestException("User already exists", HttpStatus.BAD_REQUEST);
        }

        // Mapping SignUpDto to LocalUser
        LocalUser user = new LocalUser();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(Roles.CUSTOMER);


        PasswordToken passwordToken = new PasswordToken("verificationToken", LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(tokenValidityMinutes), user);

        user.setPasswordToken(passwordToken);
        passwordToken.setUser(user);

        userRepository.save(user);
        log.info("registered");

        String verificationLink = "http://localhost:8096/verify-email" + passwordToken.getVerificationToken();

        emailSender.sendEmail(signUpDto.getEmail(), "Complete registration", "Please click here "
                + verificationLink + passwordToken.getVerificationToken());
        return ("Check your mailbox");
    }

    public Boolean verifyEmail(String verificationToken) {
        PasswordToken passwordToken = passwordTokenRepository.findByVerificationToken(verificationToken).orElseThrow(() ->
                new ResourceNotFoundException("Token is invalid", HttpStatus.NOT_FOUND));


        if (!passwordToken.isValid() || passwordToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        try {
            // Get the user associated with the token
            LocalUser user = passwordToken.getUser();

            // Enable the user and save the user
            user.setEnabled(true);
            user.setLocked(false);
            userRepository.save(user);

            passwordToken.setValid(false);
            passwordTokenRepository.save(passwordToken);

        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        log.info("User saved");
        return true;
    }


    @Override
    public AuthenticationResponse loginUser(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );
        LocalUser users1 = userRepository.findByUsernameIgnoreCase(loginDto.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found with email: " + loginDto.getUsername()));
        log.info("========> " + loginDto.getUsername());
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
    public SignUpDto createAdmin(SignUpDto signUpDto) throws BadRequestException {
        // Validate input
        if (signUpDto == null) {
            throw new BadRequestException("Provided user data is invalid", HttpStatus.BAD_REQUEST);
        }

        // Check if a user with the same email or username already exists
        if (userRepository.findByEmail(signUpDto.getEmail()).isPresent()) {
            throw new BadRequestException("User with this email already exists", HttpStatus.BAD_REQUEST);
        }
        if (userRepository.findByUsernameIgnoreCase(signUpDto.getUsername()).isPresent()) {
            throw new BadRequestException("User with this username already exists", HttpStatus.BAD_REQUEST);
        }

        try {
            LocalUser user = map2Entity(signUpDto);
            user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
            user.setRole(Roles.ADMIN);
            LocalUser savedAdmin = userRepository.save(user);
            return map2UserDTO(savedAdmin);
        } catch (Exception e) {
            throw new RuntimeException("Error while creating the admin user.", e);
        }
    }

    @Override
//    @Transactional
    public void enableUser(String verificationToken) {

        PasswordToken token = passwordTokenRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new RuntimeException("Invalid verification token"));

        LocalUser user = token.getUser();

        user.setEnabled(true);
        user.setLocked(false);
        user.setIsVerified(true);
        user.setValid(true);

        token.setValid(false);

        userRepository.save(user);
        log.info("user saved");
    }

    @Override
    public boolean isTokenValid(String verificationToken) {
        Optional<PasswordToken> confirmationTokenOpt = passwordTokenRepository.findByVerificationToken(verificationToken);

        if (confirmationTokenOpt.isPresent()) {
            return false;
        }

        PasswordToken passwordToken = confirmationTokenOpt.get();
        LocalDateTime now = LocalDateTime.now();


        if (!passwordToken.isValid() || now.isAfter(passwordToken.getExpiresAt())) {
            return false;
        }

        passwordToken.setValid(false);
        passwordTokenRepository.save(passwordToken);
        log.info("token saved");

        LocalUser user = passwordToken.getUser();
        if (user != null) {
            enableUser(user.getEmail());
            return true;
        }

        return false;
    }


    @Override
    public boolean isUserAlreadyVerified(String verificationToken) {
        Optional<PasswordToken> confirmationTokenOpt = passwordTokenRepository.findByVerificationToken(verificationToken);

        if (confirmationTokenOpt.isPresent()) {
            LocalUser user = confirmationTokenOpt.get().getUser();
            return user.isEnabled();
        }
        return false;
    }

    @Override
    public PasswordToken generatePasswordToken() {
        String alphanumericToken = RandomStringUtils.randomAlphanumeric(31);
        return PasswordToken
                .builder()
                .verificationToken(alphanumericToken)
                .build();
    }


    public static LocalUser map2Entity(SignUpDto signUpDto) {
        LocalUser user = new LocalUser();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setFirstName(signUpDto.getFirstName());
        user.setLastName(signUpDto.getLastName());
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

    public SignUpDto map2UserDTO(LocalUser user) {
        SignUpDto signUpDto = new SignUpDto();
        signUpDto.setUsername(user.getUsername());
        signUpDto.setEmail(user.getEmail());
        signUpDto.setFirstName(user.getFirstName());
        signUpDto.setLastName(user.getLastName());
        return signUpDto;

    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User with email not found "));

    }
    private String buildEmail(String name, String link) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "<span style=\"display:none;font-size:1px;color:#fff;max-height:0\"></span>\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" bgcolor=\"#0b0c0c\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" bgcolor=\"#0b0c0c\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#ffffff;text-decoration:none;vertical-align:top;display:inline-block\">Confirm your email</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#1D70B8\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi " + name + ",</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Thank you for registering. Please click on the below link to activate your account: </p><blockquote style=\"Margin:0 0 20px 0;border-left:10px solid #b1b4b6;padding:15px 0 0.1px 15px;font-size:19px;line-height:25px\"><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + link + "\">Activate Now</a> </p></blockquote>\n Link will expire in 15 minutes. <p>See you soon</p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }
//    public String signupUser(LocalUser user){
//        return "";
//    }

}



