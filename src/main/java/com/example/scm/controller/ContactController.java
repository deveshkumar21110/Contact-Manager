package com.example.scm.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @RequestMapping("/add")
    public String addContact(){

        return "user/add_contacts";
    }
}
