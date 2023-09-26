package com.example.podb.Services;


import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    AuthenticationResponse loginUser(LoginDto loginDto);
    UserDTO createAdmin(UserDTO userDto);
    void enableUser(String verificationToken);
    boolean isTokenValid(String verificationToken);
    boolean isUserAlreadyVerified(String verificationToken);
    String generateConfirmationToken();
    Boolean verifyEmail (String verificationToken);


}
