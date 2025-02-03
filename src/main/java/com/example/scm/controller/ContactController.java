package com.example.scm.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.scm.entities.Contact;
import com.example.scm.entities.User;
import com.example.scm.exceptions.ResourceNotfoundException;
import com.example.scm.forms.ContactForm;
import com.example.scm.forms.ContactSearchForm;
import com.example.scm.helper.AppConstants;
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
            HttpSession session) throws IllegalStateException, IOException {

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

        // If file is uploaded, save it
        String fileUrl = null;
        String fileName = null;
        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {
            // Saving picture in cloud
            fileName = UUID.randomUUID().toString();
            fileUrl = imageService.uploadImage(contactForm.getPicture(), fileName);
        } else {
            logger.info("No picture uploaded, skipping file upload.");
        }

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
            newContact.setFavorite(contactForm.isFavorite());
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

    @RequestMapping({ "/", "" })
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = AppConstants.SORT_ASC) String direction,
            Model model) {

        String email = userDataHelper.getEmailOfLoggedInUser();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotfoundException("User is not found: " + email));
        Page<Contact> contacts = contactService.getContactsByUser(user, page, size, sortBy, direction);

        model.addAttribute("contactSearchForm", new ContactSearchForm());
        model.addAttribute("contacts", contacts); // "contacts" attribute is added
        model.addAttribute("defaultPageSize", AppConstants.PAGE_SIZE); // "contacts" attribute is added

        // http://localhost:8080/user/contacts/?size=1&page=0
        return "user/contacts";
    }

    // search handler
    @RequestMapping("/search")
    public String searchHandler(
            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "2") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = AppConstants.SORT_ASC) String direction,
            Model model) {

        String email = userDataHelper.getEmailOfLoggedInUser();
        User user = userService.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotfoundException("User is not found: " + email));

        Page<Contact> contacts = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            contacts = contactService.searchByName(contactSearchForm.getKeyword(), page, size, sortBy, direction, user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            contacts = contactService.searchByEmail(contactSearchForm.getKeyword(), page, size, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            page = (int) page;
            contacts = contactService.searchByPhoneNumber(contactSearchForm.getKeyword(), page, size, sortBy, direction,
                    user);
        }

        logger.info("Page size: {}", contacts.getSize());
        logger.info("Current page number: {}", contacts.getNumber());
        logger.info("Current page (number of elements) per page: {}", contacts.getSize());
        logger.info("Total number of pages: {}", contacts.getTotalPages());
        logger.info("content: {}", contacts.getContent());

        model.addAttribute("defaultPageSize", AppConstants.PAGE_SIZE); // "contacts" attribute is added
        model.addAttribute("contactSearchForm", contactSearchForm);
        model.addAttribute("contacts", contacts);
        return "user/search";
    }

    @RequestMapping("/delete/{contactId}")
    public String deleteContact(
            @PathVariable("contactId") String contactId,
            HttpSession session) {
        contactService.deleteContact(contactId);
        session.setAttribute("message",
                Message
                        .builder()
                        .content("")
                        .build());
        return "redirect:/user/contacts";
    }

    @GetMapping("/view/{contactId}")
    public String updateContactFormView(@PathVariable("contactId") String contactId, Model model) {

        Contact contact = contactService.getContactByContactId(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAbout(contact.getDescription());
        contactForm.setAddress(contact.getAddress());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setPictureUrl(contact.getPicture());

        model.addAttribute("contactId", contactId);
        model.addAttribute("updateContactForm", contactForm);

        return "user/update_contact_view";
    }

    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult bindingResult,
            Model model) {

        // update the contact
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        var con = contactService.getContactByContactId(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getAbout());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(contactForm.getWebsiteLink());

        // process image:

        if (contactForm.getPicture() != null && !contactForm.getPicture().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getPicture(), fileName);
            con.setFilePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPictureUrl(imageUrl);

        } else {
            logger.info("file is empty");
        }

        var updateCon = contactService.updateContact(con);
        logger.info("updated contact {}", updateCon);

        model.addAttribute("message", Message.builder().content("Contact Updated !!").type(MessageType.green).build());

        return "redirect:/user/contacts/view/" + contactId;
    }


}
