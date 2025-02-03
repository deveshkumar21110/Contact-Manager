package com.example.scm.auth.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.scm.entities.User;
import com.example.scm.helper.Message;
import com.example.scm.helper.MessageType;
import com.example.scm.services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String emailToken, HttpSession session) {
        System.out.println("verify email controller called");

        String page = userService.getUserByEmailToken(emailToken)
                .filter(user -> emailToken.equals(user.getEmailToken()))
                .map(user -> {
                    User mutableUser = new User(user);  // Create a mutable copy of user
                    mutableUser.setEmailToken(null);
                    mutableUser.setEnabled(true);
                    userService.saveUser(mutableUser);
                    session.setAttribute("message",
                            Message.builder()
                                    .content("Your Email is verified, now you can login.")
                                    .type(MessageType.green)
                                    .build());
                    return "emailVerified";
                })
                .orElseGet(() -> {
                    session.setAttribute("message",
                            Message.builder()
                                    .content("Email verification failed.")
                                    .type(MessageType.red)
                                    .build());
                    return "emailVerifyFailed";
                });

        return page; // Ensure the correct page is returned
    }
}
