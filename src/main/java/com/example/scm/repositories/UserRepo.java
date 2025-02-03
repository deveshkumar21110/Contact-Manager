package com.example.scm.repositories;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;



@Repository
public interface UserRepo extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailToken(String emailToken);


}
