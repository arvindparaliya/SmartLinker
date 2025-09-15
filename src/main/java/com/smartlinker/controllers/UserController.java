package com.smartlinker.controllers;

import java.time.LocalDateTime;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;
import com.smartlinker.forms.ContactForm;
import com.smartlinker.forms.UserForm;
import com.smartlinker.helpers.Helper;
import com.smartlinker.repositories.ContactRepo;
import com.smartlinker.services.ContactService;
import com.smartlinker.services.EmailService;
import com.smartlinker.services.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    // private Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private ContactRepo contactRepo;

    // @Autowired
    // private UserRepo userRepo;

    @Autowired
    private ContactService contactService;

    @Autowired
    private EmailService emailService;
    // user dashbaord page

    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication, LocalDateTime date) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        long totalContacts = contactService.getTotalContactsByUser(user);
        long favoriteContacts = contactRepo.countByUserAndFavoriteTrue(user);

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        long newContacts = contactRepo.countByUserAndCreatedAtAfter(user, oneWeekAgo);

        List<Contact> recentContacts = contactRepo.findTop4ByUserOrderByCreatedAtDesc(user);

        model.addAttribute("recentContacts", recentContacts);
        model.addAttribute("totalContacts", totalContacts);
        model.addAttribute("favoriteContacts", favoriteContacts);
        model.addAttribute("newContacts", newContacts);
        model.addAttribute("recentContacts", recentContacts);

        if (recentContacts == null || recentContacts.isEmpty()) {
            System.out.println("Top 5 contacts returned EMPTY or NULL");
        } else {
            System.out.println("Top 5 contacts found: " + recentContacts.size());
            for (Contact c : recentContacts) {
                System.out.println("Contact: " + c.getName() + ", Created At: " + c.getCreatedAt());
            }
        }
        return "user/dashboard";
    }

    @GetMapping("/categories")
    public String categoriesPage(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        // Fetch counts
        long favoriteCount = contactRepo.countByUserAndFavoriteTrue(user);
        long recentCount = contactRepo.countByUserAndCreatedAtAfter(user, LocalDateTime.now().minusDays(7));
        long linkedinCount = contactRepo.countByUserAndLinkedInLinkIsNotNullAndLinkedInLinkNot(user, "");
        long websiteCount = contactRepo.countByUserAndWebsiteLinkIsNotNullAndWebsiteLinkNot(user, "");
        long missingEmailCount = contactRepo.countByUserAndEmailIsNullOrEmailEquals(user, "");

        long missingPhoneOrAddressCount = contactRepo.countMissingPhoneOrAddress(user, "");

        model.addAttribute("favoriteCount", favoriteCount);
        model.addAttribute("recentCount", recentCount);
        model.addAttribute("linkedinCount", linkedinCount);
        model.addAttribute("websiteCount", websiteCount);
        model.addAttribute("missingEmailCount", missingEmailCount);
        model.addAttribute("missingPhoneOrAddressCount", missingPhoneOrAddressCount);

        return "user/categories";
    }

    // Show user profile page
    @GetMapping("/profile")
    public String userProfile(Model model, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);
        model.addAttribute("loggedInUser", user);

        return "user/profile";
    }

    // Show update profile form
    @GetMapping("/profile/view/{userId}")
    public String updateProfileFormView(
            @PathVariable("userId") String userId,
            Model model) {

        User user = userService.getUserById(userId).orElse(null);
        UserForm userForm = new UserForm();

        if (user != null) {
            userForm.setName(user.getName());
            userForm.setEmail(user.getEmail());
            userForm.setPhoneNumber(user.getPhoneNumber());
            userForm.setPicture(user.getProfilePic());
        }

        model.addAttribute("userForm", userForm);
        model.addAttribute("userId", userId);

        return "user/update_profile_view";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @ModelAttribute("user") User formUser,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String email = Helper.getEmailOfLoggedInUser(authentication);
        User loggedInUser = userService.getUserByEmail(email);

        if (loggedInUser == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
            return "user/profile";
        }

        // Call service layer
        userService.updateProfile(loggedInUser.getId(), formUser, profileImage);

        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "user/profile";
    }

    // Show contact form
    @GetMapping("/feedback")
    public String feedbackPage(Model model, Authentication auth) {
        String email = Helper.getEmailOfLoggedInUser(auth);
        User user = userService.getUserByEmail(email);
        model.addAttribute("loggedInUser", user);
        model.addAttribute("contactForm", new ContactForm());
        return "user/feedback"; // must match your actual template file
    }

    // Handle contact form submission
    @PostMapping("/feedback")
    public String handleFeedbackSubmission(
            @ModelAttribute ContactForm contactForm,
            RedirectAttributes redirectAttributes) {
        try {
            emailService.sendContactMail(contactForm);
            redirectAttributes.addFlashAttribute("successMessage", "Message sent successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Oops! Failed to send message.");
        }
        return "redirect:/user/feedback";
    }

}