package com.example.scm.helper;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class GitHubEmailFetcher {

    private static final Logger logger = LoggerFactory.getLogger(GitHubEmailFetcher.class);

    /**
     * Fetch the primary email of a GitHub user using the OAuth2AuthenticationToken.
     *
     * @param token the OAuth2AuthenticationToken of the authenticated GitHub user
     * @return the primary email address, or a default placeholder if not found
     */
    public static String fetchPrimaryEmail(OAuth2AuthenticationToken token) {
        String email = null;

        // Extract the access token from the OAuth2AuthenticationToken
        String accessToken = token.getPrincipal().getAttribute("access_token");
        if (accessToken == null) {
            logger.error("Access token is null. Cannot fetch email.");
            return "no-email-provided@gmail.com";
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            String emailEndpoint = "https://api.github.com/user/emails";
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                emailEndpoint, HttpMethod.GET, entity,
                new ParameterizedTypeReference<>() {}
            );

            // Extract the primary email from the response
            List<Map<String, Object>> emails = response.getBody();
            if (emails != null) {
                for (Map<String, Object> emailEntry : emails) {
                    if (Boolean.TRUE.equals(emailEntry.get("primary"))) {
                        email = emailEntry.get("email").toString();
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching email from GitHub: " + e.getMessage(), e);
        }

        // Assign a default email if no primary email is found
        return email != null ? email : "no-email-provided@gmail.com";
    }
}
