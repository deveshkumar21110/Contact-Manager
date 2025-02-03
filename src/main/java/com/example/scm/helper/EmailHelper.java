package com.example.scm.helper;

import org.springframework.stereotype.Component;

@Component
public class EmailHelper {
    
    public static String getLinkForAuthentication(String emailToken){
        String verifyLink = "http://localhost:8080/auth/verify-email?token=" + emailToken ;
        return verifyLink;
    }
}
