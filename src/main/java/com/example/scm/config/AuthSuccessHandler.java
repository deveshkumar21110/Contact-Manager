package com.example.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.example.scm.entities.Providers;
import com.example.scm.entities.User;
import com.example.scm.helper.AppConstants;
import com.example.scm.helper.GitHubEmailFetcher;
import com.example.scm.services.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthSuccessHandler implements AuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserService userService;

    public AuthSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // identify the Auth Provider -> get the user's email -> get the user's details
        // from the database -> user not found -> create a new user -> save the user ->
        // redirect to the user's profile page

        logger.info("{}",this.getClass(), "is called");

        String email = null;
        if (authentication instanceof OAuth2AuthenticationToken) {
            // identify the provider
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            String clientIdName = token.getAuthorizedClientRegistrationId().toLowerCase(); // google or github
            logger.info("Authentication Provider : " + clientIdName); // logging client name

            // extracting oauthUser from authentication object
            DefaultOAuth2User oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

            // finding email of github users
            if ("github".equals(clientIdName)) {
                email = GitHubEmailFetcher.fetchPrimaryEmail(token);
                logger.info("email: {}", email);
            } else if ("google".equals(clientIdName)) {
                email = oauthUser.getAttribute("email");
                logger.info("email: {}", email);
            }

            // if email is null then redirect to /login.
            if (email == null) {
                logger.warn("Email not found for user: " + oauthUser.getName());
                response.sendRedirect("/login");
                return;
            }

            User user = userService.getUserByEmail(email).orElse(null);

            // if user is null then create new User and saving oauth user
            if (user == null) {
                user = new User();
                user.setRoleList(List.of(AppConstants.ROLE_USER));
                user.setEmailVerified(true);
                user.setEnabled(true);
                user.setPassword(new BCryptPasswordEncoder().encode(UUID.randomUUID().toString()));
                user.setEmail(email);
                user.setProfilePic(oauthUser.getAttribute("picture").toString());
                user.setName(oauthUser.getAttribute("name").toString());
                user.setProviderId(oauthUser.getName());
                user.setProvider("google".equals(clientIdName) ? Providers.GOOGLE : Providers.GITHUB);
                user.setAbout("This account is created using " + clientIdName + ".");
                userService.saveUser(user);
            }
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {

            logger.error("Authentication is not an instance of UsernamePasswordAuthenticationToken");
        }

        response.sendRedirect("/user/profile");
    }

}