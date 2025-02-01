package com.example.scm.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.scm.entities.Contact;
import com.example.scm.entities.User;

@Repository
public interface ContactRepo extends JpaRepository<Contact, String> {

    @Query("SELECT c FROM contact c WHERE c.user.id = :userId")
    List<Contact> findByUserId(@Param("userId") String userId);

    List<Contact> findByNameOrEmailOrPhoneNumber(String name, String email, String phoneNumber);

    Page<Contact> findByUser(User user,Pageable pageable);

}