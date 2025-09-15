package com.smartlinker.controllers;

import com.smartlinker.entities.User;
import com.smartlinker.forms.ContactForm;
import com.smartlinker.forms.UserForm;
import com.smartlinker.helpers.Message;
import com.smartlinker.helpers.MessageType;
import com.smartlinker.services.EmailService;
import com.smartlinker.services.UserService;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    //Home page redirection
    @GetMapping("/")
    public String index() {
        return "redirect:/home";
    }

    //Home page handler
    @RequestMapping("/home")
    public String home(Model model) {
        System.out.println("Home page handler");
        model.addAttribute("name", "SpringBoot Project");
        model.addAttribute("youtubeChannel", "Code with Arvind Paraliya");
        model.addAttribute("githubRepo", "https://github.com/arvindparaliya/");
        return "home";
    }

    //About page handler
    @RequestMapping("/about")
    public String aboutPage(Model model) {
        model.addAttribute("isLogin", true);
        System.out.println("About page loading");
        return "about";
    }

    //Services page handler
    @RequestMapping("/services")
    public String servicesPage() {
        System.out.println("Services page loading");
        return "services";
    }

    //Show teh contact form
    @GetMapping("/contact")
    public String showContactForm(Model model) {
        model.addAttribute("contactForm", new ContactForm());
        return "contact";
    }

    //Handle the contact form submission
    @PostMapping("/contact")
    public String handleFormSubmission(
            @ModelAttribute ContactForm contactForm,
            RedirectAttributes redirectAttributes) {
        try {
            emailService.sendContactMail(contactForm);
            redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Oops! Failed to send message.");
        }
        return "redirect:/contact";
    }

    //Show login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    //Show registration page with empty form
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userForm", new UserForm());
        return "register";
    }

   //Process registration form submission
   @PostMapping("/do-register")
    public String processRegistration(
        @ModelAttribute("userForm") UserForm userForm,
        BindingResult bindingResult,
        HttpSession session,
        Model model) {

    System.out.println("Processing registration");

    if (bindingResult.hasErrors()) {
        return "register";
    }

    User user = new User();
    user.setName(userForm.getName());
    user.setEmail(userForm.getEmail());

    // pass encode 
    user.setPassword(passwordEncoder.encode(userForm.getPassword()));

    user.setAbout(userForm.getAbout());
    user.setPhoneNumber(userForm.getPhoneNumber());
    user.setEnabled(true); 
    user.setProfilePic("https://example.com/default-profile.jpg");

    try {
    userService.saveUser(user);  
    session.setAttribute("message",
        new Message("Registration Successful! Please check your email to verify your account.", MessageType.green));
    return "redirect:/login";  

    } catch (Exception ex) {
        model.addAttribute("userForm", userForm);
        session.setAttribute("message",
                new Message("This email is already registered. Please log in instead." , MessageType.red));
                System.out.println(ex.getMessage());
        return "register";
    }
}
}