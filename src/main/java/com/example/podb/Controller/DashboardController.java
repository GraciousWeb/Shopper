package com.example.podb.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

public class DashboardController {
    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        // You can fetch additional data for the user here if needed.
        return new ModelAndView("dashboard");
    }

}
