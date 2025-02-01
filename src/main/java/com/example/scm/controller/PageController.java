package com.example.scm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    Logger logger  = LoggerFactory.getLogger(this.getClass());

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

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String processRegister(
            @Valid @ModelAttribute("defaultFormAttribute") UserForm newForm,
            BindingResult rBindingResult,
            Model model,
            HttpSession session) {

        logger.info("Processing registration");
        if (rBindingResult.hasErrors()) {
            // Print all validation errors to the console for debugging
            rBindingResult.getAllErrors().forEach((e) -> {
                System.out.println("Error: " + e.getDefaultMessage());
            });

            // Add the form with errors to the model to retain the input values
            model.addAttribute("defaultFormAttribute", newForm);

            // Return to the registration page with the form data and errors
            return "register";
        }

        // If no errors, save the user
        userService.saveUserFromUserForm(newForm);

        // Log the encoded password
        System.out.println("Encoded password: " + newForm.getPassword());

        // Set a success message in the session
        Message message = new Message("Registration successful", MessageType.green);
        System.out.println("Message Type: " + message.getType());
        session.setAttribute("message", message);

        return "redirect:/register"; // Redirect after successful registration
    }
}
