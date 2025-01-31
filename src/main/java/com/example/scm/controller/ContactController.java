package com.example.scm.controller;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;
import com.example.scm.forms.ContactForm;
import com.example.scm.helper.Message;
import com.example.scm.helper.MessageType;
import com.example.scm.helper.UserDataHelper;
import com.example.scm.services.ContactService;
import com.example.scm.services.ImageService;
import com.example.scm.services.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    @Autowired
    private UserDataHelper userDataHelper;

    private final UserService userService;
    private final ContactService contactService;
    private final ImageService imageService;

    @Autowired
    public ContactController(UserService userService, ContactService contactService, ImageService imageService) {
        this.userService = userService;
        this.contactService = contactService;
        this.imageService = imageService;
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping("/add")
    public String addContact(Model model) {
        ContactForm contactForm = new ContactForm();
        // contactForm.setName("aman");
        contactForm.setEmail("aman@gmail.com");
        contactForm.setPhoneNumber("1234567890");
        contactForm.setAbout("I am a software developer");
        contactForm.setAddress("Delhi, India");
        contactForm.setFavorite(true);
        contactForm.setWebsiteLink("https://www.google.com");
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult rBindingResult,
            Authentication authentication, HttpSession session) throws IllegalStateException, IOException {

        // validation
        if (rBindingResult.hasErrors()) {
            session.setAttribute("message",
                    Message.builder().content("Please fill the form properly.").type(MessageType.red).build());
            return "user/add_contact";
        }
        // contactForm -> contact
        String email = userDataHelper.getEmailOfLoggedInUser();
        Optional<User> userOptional = userService.getUserByEmail(email);
        logger.info("savecontact called");

        // saving picture in cloud
        String fileName = UUID.randomUUID().toString();
        String fileUrl = imageService.uploadImage(contactForm.getPicture(), fileName);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            Contact newContact = new Contact();
            newContact.setName(contactForm.getName());
            newContact.setEmail(contactForm.getEmail());
            newContact.setPhoneNumber(contactForm.getPhoneNumber());
            // newContact.setPicture(picturePath);
            newContact.setPicture(fileUrl);
            logger.info("Generated file public ID: {}", fileName);
            newContact.setFilePublicId(fileName);
            newContact.setDescription(contactForm.getAbout());
            newContact.setAddress(contactForm.getAddress());
            newContact.setFavourite(contactForm.isFavorite());
            newContact.setWebsiteLink(contactForm.getWebsiteLink());
            newContact.setUser(user);
            logger.info("Saving contact with Public ID: {}", newContact.getFilePublicId());
            contactService.saveContact(newContact);
            logger.info("Contact saved successfully with ID: {}", newContact.getId());

            session.setAttribute("message",
                    Message.builder().content("You have successfully added a new contact.").type(MessageType.green)
                            .build());
            logger.info(newContact.toString());

            return "redirect:/user/contacts/add";
        } else {
            // Handle the case where the user is not found
            logger.error("User not found with email: " + email);
            return "redirect:/error";
        }
    }
}
