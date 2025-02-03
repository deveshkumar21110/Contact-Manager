package com.example.scm.services;

import java.util.List;
import java.util.Optional;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;
import com.example.scm.forms.UserForm;

public interface UserService {

    User saveUser(User user);

    User saveOAuthUser(User user);
    
    User saveUserFromUserForm(UserForm user);

    Optional<User> getUserById(String id);

    Optional<User> getUserByEmail(String email);

    void deleteUserById(String id);

    boolean isUserPresent(String id);

    Optional<User> updateUser(User user);

    boolean isUserPresentByEmail(String email);

    List<User> getAllUsers();

    Optional<User> getUserByEmailToken(String emailToken);

}
