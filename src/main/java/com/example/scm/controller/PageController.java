package com.example.scm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.example.scm.forms.UserForm;
import com.example.scm.helper.Message;
import com.example.scm.helper.MessageType;
import com.example.scm.services.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class PageController {

    private final UserService userService;

    @Autowired
    public PageController(UserService userService) {
        this.userService = userService;
    }


    // about route
    @RequestMapping("/about")
    public String aboutPage() {
        System.out.println("about page loading");
        return "about";
    }

    // services route
    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services page loading");
        return "services";
    }

    @RequestMapping({ "/home", "/" })
    public String homePage() {
        System.out.println("Home page loading");
        return "home";
    }

    @RequestMapping("/contact")
    public String contactPage() {
        System.out.println("Contact page loading");
        return "contact";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "login";
    }


    @RequestMapping("/register")
    public String register(Model model) {
        // Adding an empty UserForm object to the model
        UserForm dForm = new UserForm();
        dForm.setName("Aman");
        dForm.setEmail("aman@gmail.com");
        dForm.setPassword("12345");
        dForm.setAbout("I am a web developer");
        dForm.setPhoneNumber("1234567890");

        model.addAttribute("defaultFormAttribute", dForm); // Changed from "defaultUser" to "dForm"
        
        System.out.println("register page loading");
        return "register";
    }

    @RequestMapping(value = "/do-register", method = RequestMethod.POST)
    public String processRegister(
            @Valid @ModelAttribute("defaultFormAttribute") UserForm newForm, // Changed from "defaultUser" to "dForm"
            BindingResult rBindingResult,
            HttpSession session) {

        if (rBindingResult.hasErrors()) {
            // If there are validation errors, return to the registration page
            return "register";
        }
        // Save user from userForm
        userService.saveUserFromUserForm(newForm);

        // Log the encoded password
        System.out.println("Encoded password: " + newForm.getPassword());

        // Show success message
        Message message = new Message("Registration successful", MessageType.red); // Ensure MessageType is correct
        System.out.println("Message Type: " + message.getType());  // Log the message type to the console
        session.setAttribute("message", message);


        return "redirect:/register";
    }
}
