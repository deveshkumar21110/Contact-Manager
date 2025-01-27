package com.example.scm.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Component;

import com.example.scm.auth.model.UserPrincipal;

@Component
public class UserDataHelper {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @SuppressWarnings("null")
    public String getEmailOfLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            String clientId = oauthToken.getAuthorizedClientRegistrationId();
            logger.info("clientId: " + clientId);

            String username = null;
            DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();

            if (clientId.equalsIgnoreCase("google")) {
                logger.info("user logged in by google");
                username = oAuth2User.getAttribute("email");
            } else if (clientId.equalsIgnoreCase("github")) {
                logger.info("user logged in by github");
                username = oAuth2User.getAttribute("email");
            }

            return username;
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            logger.error("data of Username password authenticated user");
            // extract email of UsernamePasswordAuthenticationToken users
            Object principal = authentication.getPrincipal();
            
            UserPrincipal userPrincipal = (UserPrincipal) principal;
            logger.error("username/email: " + userPrincipal.getUsername());
            return userPrincipal.getUsername();
            
        } else {
            logger.error("No Provider available");
            return null;
        }
    }
}
