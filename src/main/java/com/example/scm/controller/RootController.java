package com.example.scm.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.scm.entities.User;
import com.example.scm.helper.UserDataHelper;
import com.example.scm.services.UserService;


@ControllerAdvice
public class RootController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserDataHelper userDataHelper;

    

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addLoggedInUserInfo(Model model, Authentication authentication) {
        // Check if the user is authenticated before calling the addLoggedInUserInfo
        // method.
        logger.info("{},{}",this.getClass(), "is called");
        authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return;
            // throw new IllegalStateException("User is not authenticated");

        }

        String username = userDataHelper.getEmailOfLoggedInUser();

        if (username == null || username.isEmpty()) {
            logger.warn("Logged-in user's email is null or empty");
            return; // Avoid querying the database with a null email
        }
        
        logger.info("Adding loggedIn user info to model | Email: {}", username);


        Optional<User> loggedInUser = userService.getUserByEmail(username);
        logger.info(loggedInUser.get().getName());
        logger.info(loggedInUser.get().getEmail());
        // model.addAttribute(loggedInUser, model); // "loggedInUser" is not act as
        // Object
        // Always add actual data objects (like User) to the model, not the Model
        // itself.
        model.addAttribute("loggedInUser", loggedInUser.get());
        loggedInUser.ifPresent(user -> model.addAttribute("loggedInUser", user));
    }

}
