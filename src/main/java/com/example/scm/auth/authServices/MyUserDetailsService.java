package com.example.scm.auth.authServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.scm.auth.model.UserPrincipal;
import com.example.scm.entities.User;
import com.example.scm.repositories.UserRepo;

import java.util.logging.Logger;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    private static final Logger logger = Logger.getLogger(MyUserDetailsService.class.getName());

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.info("Loading user by email: " + email);
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        
        // logger.info("User found: " + user.getEmail());
        // logger.info("User enabled: " + user.isEnabled());
        // logger.info("User password: " + user.getPassword());

        // // Log the raw password for comparison (for debugging purposes only, remove in production)
        logger.info("Raw password for comparison: " +  user.getPassword());

        return new UserPrincipal(user);
    }

}
