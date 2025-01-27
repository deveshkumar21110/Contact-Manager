package com.example.scm.controller;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String userDashboard(Authentication authentication, Model model) {
        logger.info("dashboard request");
        return "/user/dashboard";
    }

    @GetMapping("/profile")
    public String userProfile(Model model) {
        // The model will already have the "loggedInUser" attribute added by RootController
        return "/user/profile"; // Return the name of the Thymeleaf template
    }

    // user add contact page

    // user view contact page

    // user update contact page
}