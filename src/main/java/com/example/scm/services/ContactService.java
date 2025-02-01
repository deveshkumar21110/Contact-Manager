package com.example.scm.services;

import java.util.List;

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

    List<Contact> getContactsByUser(User user);
}
