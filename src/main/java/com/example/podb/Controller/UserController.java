package com.example.podb.Controller;

import com.example.podb.DTO.AuthenticationResponse;
import com.example.podb.DTO.LoginDto;
import com.example.podb.DTO.SignUpDto;
import com.example.podb.Model.LocalUser;
import com.example.podb.Services.UserServices;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

//@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
//@RequestMapping("api/v1/users")
public class UserController {

    private final UserServices userService;
    private final AuthenticationManager authenticationManager;
    @GetMapping("/register")
    public ModelAndView signUp() {
        return new ModelAndView("index")
                .addObject("signUpDto", new SignUpDto());
    }

    @PostMapping("/register")
    public ModelAndView signUp(SignUpDto signUpDTO){
        String user = userService.registerUser(signUpDTO);
        if(user!=null){
            return new ModelAndView("index")
                    .addObject("login-form", new LoginDto())
                    .addObject("userMessage", "Sign up successful, please login.");
        }
        return new ModelAndView("index")
                .addObject("signUpDto",  new SignUpDto());
    }
    @GetMapping("/verify-account")
    public ModelAndView verifyEmail(@RequestParam("token") String token) {
        ModelAndView modelAndView = new ModelAndView();
        log.info("i got here ");

        Boolean isVerified = userService.verifyEmail(token);

        if (isVerified) {
            modelAndView.setViewName("verifyEmail");
            modelAndView.addObject("message", "Your email has been successfully verified!");
        } else {
            modelAndView.setViewName("verificationFailed");

            modelAndView.addObject("message", "Email verification failed or token is invalid.");
        }

        return modelAndView;
    }
    @PostMapping("/login")
    public ModelAndView login(LoginDto loginDTO, HttpServletRequest request) {
        try {
            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
            );
            // Set the authentication in the SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to a dashboard
            return new ModelAndView("redirect:/dashboard");

        } catch (AuthenticationException e) {
            return new ModelAndView("index")
                    .addObject("errorMessage", "Invalid username or password");
        }

    }


    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        // You can fetch additional data for the user here if needed.
        return new ModelAndView("dashboard");
    }




//    @GetMapping("login-form")
//    public ModelAndView index() {
//        return new ModelAndView("index");
//    }

//
//    @PostMapping("register")
//    public ResponseEntity<SignUpDto> registerUser(@RequestBody SignUpDto signUpDto) {
//        SignUpDto registeredUser = userServices.registerUser(signUpDto);
//        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
//    }
//
//    @PostMapping("login")
//    public ResponseEntity<AuthenticationResponse> loginUser(@RequestBody LoginDto loginDto) {
//        AuthenticationResponse response = userServices.loginUser(loginDto);
//        return ResponseEntity.ok().body(response);
//    }


}
