package com.example.scm.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;

public interface ContactService {
    Contact saveContact(Contact contact);

    Contact updateContact(Contact contact);

    List<Contact> getAllContacts();

    Contact getContactByContactId(String id);

    List<Contact> getContactByUserId(String userId);

    void deleteContact(String id);

    List<Contact> searchContact(String name, String email, String phoneNumber);

    Page<Contact> getContactsByUser(User user,int page, int size,String sortBy, String direction);

    // search contact
    Page<Contact> searchByName(String nameKeyword,int page, int size,String sortBy, String direction,User user);
    Page<Contact> searchByEmail(String emailKeyword,int page, int size,String sortBy, String direction,User user);
    Page<Contact> searchByPhoneNumber(String phonekeyword,int page, int size,String sortBy, String direction,User user);
}
