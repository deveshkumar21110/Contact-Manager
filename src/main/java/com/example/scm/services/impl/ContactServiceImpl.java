package com.example.scm.services.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;
import com.example.scm.exceptions.ResourceNotfoundException;
import com.example.scm.repositories.ContactRepo;
import com.example.scm.services.ContactService;

import jakarta.transaction.Transactional;

@Service
public class ContactServiceImpl implements ContactService{

    private ContactRepo repo;

    public ContactServiceImpl(ContactRepo repo){ 
        this.repo = repo;
    }

    @Override
    @Transactional
    public Contact saveContact(Contact contact) {
        String contactId = UUID.randomUUID().toString();
        contact.setId(contactId);
        return repo.save(contact);
    }

    @Override
    public Contact updateContact(Contact contact) {
        return contact;
    }

    @Override
    public List<Contact> getAllContacts() {
        return repo.findAll();
    }

    @Override
    public Contact getContactByContactId(String id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotfoundException("Contact not found of this id: " + id));
    }
    @Override
    public List<Contact> getContactByUserId(String id) {
        return repo.findByUserId(id);
    }

    @Override
    public void deleteContact(String id) {
        repo.deleteById(id);

    }

    @Override
    public List<Contact> searchContact(String name, String email, String phoneNumber) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchContact'");
    }

    @Override
    public List<Contact> getContactsByUser(User user) {
        return repo.findByUser(user);
    }
    
}
