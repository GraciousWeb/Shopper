package com.example.podb.Controller;

import com.example.podb.Services.UserServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequiredArgsConstructor
@Slf4j
public class VerificationController {
    private final UserServices userService;

    @GetMapping("/verify-email")
    public ModelAndView verifyEmail(@RequestParam String verificationToken) {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("verifyEmail");

        try {
            if (userService.isTokenValid(verificationToken)) {
                if (userService.isUserAlreadyVerified(verificationToken)) {
                    modelAndView.addObject("success", false);
                    modelAndView.addObject("error", "Email is already verified. No action required.");
                } else {
                    userService.enableUser(verificationToken);
                    modelAndView.addObject("success", true);
                    modelAndView.addObject("message", "Email successfully verified.");

                    return new ModelAndView("index")
                            .addObject("/login");
                }
            } else {
                modelAndView.addObject("success", false);
                modelAndView.addObject("error", "Invalid or expired verification token.");
            }
        } catch (Exception e) {
            log.error("Error occurred during email verification.", e);

            modelAndView.addObject("success", false);
            modelAndView.addObject("error", "An error occurred during verification. Please try again later.");
        }

        return modelAndView;
    }


}
