package com.example.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.scm.entities.Contact;
import com.example.scm.services.ContactService;

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @Autowired
    private ContactService contactService;

    @GetMapping("/contact/{contactId}")
    public Contact geContact(@PathVariable String contactId){
        return contactService.getContactByContactId(contactId);
    }
}
