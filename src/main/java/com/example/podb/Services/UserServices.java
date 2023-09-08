package com.example.podb.Services;


import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.SignUpDto;
import com.example.podb.Model.LocalUser;
import com.example.podb.token.PasswordToken;

import java.util.Optional;

public interface UserServices {
    String registerUser(SignUpDto signUpDto);
    AuthenticationResponse loginUser(LoginDto loginDto);
    SignUpDto createAdmin(SignUpDto userDto);
    void enableUser(String verificationToken);
    boolean isTokenValid(String verificationToken);
    boolean isUserAlreadyVerified(String verificationToken);
    PasswordToken generatePasswordToken();
    Boolean verifyEmail (String verificationToken);


}
