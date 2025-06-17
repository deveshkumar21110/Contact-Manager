package com.example.scm.services.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.scm.exceptions.ResourceNotfoundException;
import com.example.scm.forms.UserForm;
import com.example.scm.helper.AppConstants;
import com.example.scm.helper.EmailHelper;
import com.example.scm.entities.Contact;
import com.example.scm.entities.User;
import com.example.scm.repositories.UserRepo;
import com.example.scm.services.MailService;
import com.example.scm.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MailService mailService;

    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(BCryptPasswordEncoder encoder) {
        this.encoder = encoder;
    }

    @Override
    public User saveUser(User user) {
        String id = UUID.randomUUID().toString();
        user.setId(id);
        String encodedPassword = encoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        user.setRoleList(List.of(AppConstants.ROLE_USER));

        // Log the encoded password
        logger.info("Encoded password for user {}: {}", user.getEmail(), encodedPassword);

        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepo.findByEmail(email).orElse(null));
    }

    @Override
    public void deleteUserById(String id) {
        User user = userRepo.findById(id).orElseThrow(()-> new ResourceNotfoundException("User not found with id: " + id));
        userRepo.delete(user);
    }

    @Override
    public boolean isUserPresent(String id) {
        User user = userRepo.findById(id).orElseThrow(() ->new ResourceNotfoundException("User not found with id: " + id));
        return user != null ? true : false;
    }

    @Override
    public Optional<User> updateUser(User user) {
        String id = user.getId();
        User oldUser = userRepo.findById(id).orElseThrow(()-> new ResourceNotfoundException("User not found with id: " + id));
        // updating user
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setPassword(encoder.encode(user.getPassword())); // Encode the password
        oldUser.setAbout(user.getAbout());
        oldUser.setPhoneNumber(user.getPhoneNumber());
        oldUser.setProfilePic(user.getProfilePic());
        oldUser.setEnabled(user.isEnabled());
        oldUser.setEmailVerified(user.isEmailVerified());
        oldUser.setPhoneVerified(user.isPhoneVerified());
        oldUser.setProvider(user.getProvider());
        oldUser.setProviderId(user.getProviderId());
        
        // save user in db
        User savedUser = userRepo.save(oldUser);
        return Optional.ofNullable(savedUser);
    }

    @Override
    public boolean isUserPresentByEmail(String email) {
        User user = userRepo.findByEmail(email).orElseThrow(()-> new ResourceNotfoundException("User not found with id: " + email));
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User saveUserFromUserForm(UserForm userForm) {
        User user = new User();
        user.setName(userForm.getName());
        user.setEmail(userForm.getEmail());
//        user.setPassword(userForm.getPassword()); // Encode the password
        user.setAbout(userForm.getAbout());
        user.setPhoneNumber(userForm.getPhoneNumber());
        String emailToken = UUID.randomUUID().toString();// email token
        user.setEmailToken(emailToken);
        User savedUser = saveUser(user);
        String verifyLink = EmailHelper.getLinkForAuthentication(emailToken);
        mailService.sendEmail(user.getName(),"Smart Contact Manager Email Verification",verifyLink);
        return savedUser;
    }

    @Override
    public User saveOAuthUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public Optional<User> getUserByEmailToken(String emailToken) {
        return userRepo.findByEmailToken(emailToken);
    }
}
