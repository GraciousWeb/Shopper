package com.example.podb.Controller;

import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.UserDTO;
import com.example.podb.Services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }
    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody LoginDto loginDto) {
        AuthenticationResponse response = userService.loginUser(loginDto);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("verify")
    public ResponseEntity<?> verifyEmail(@RequestParam String verificationToken){
        Boolean isVerified = userService.verifyEmail(verificationToken);
        if (isVerified){
            return ResponseEntity.ok("User successfully verified");
        }
        log.info("Verified!");
        return ResponseEntity.badRequest().body("invalid verification token");
    }
    @GetMapping("/is-token-valid/{token}")
    public ResponseEntity<Boolean> isTokenValid(@PathVariable String token) {
        Boolean isValid = userService.isTokenValid(token);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/is-user-verified/{token}")
    public ResponseEntity<Boolean> isUserAlreadyVerified(@PathVariable String token) {
        Boolean isVerified = userService.isUserAlreadyVerified(token);
        return ResponseEntity.ok(isVerified);
    }


}
