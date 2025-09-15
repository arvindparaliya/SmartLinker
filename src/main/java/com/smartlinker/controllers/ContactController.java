package com.smartlinker.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;
import com.smartlinker.forms.ContactForm;
import com.smartlinker.forms.ContactSearchForm;
import com.smartlinker.helpers.AppConstants;
import com.smartlinker.helpers.Helper;
import com.smartlinker.helpers.Message;
import com.smartlinker.helpers.MessageType;
import com.smartlinker.repositories.UserRepo;
import com.smartlinker.services.ContactService;
import com.smartlinker.services.ImageService;
import com.smartlinker.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/user/contacts")
public class ContactController {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(ContactController.class);

    // @Autowired
    // private ContactService contactService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    private final ContactService contactService;
    private final UserRepo userRepo;
    
    public ContactController(ContactService contactService, UserRepo userRepo) {
        this.contactService = contactService;
        this.userRepo = userRepo;
    }
    

    @RequestMapping("/add")
    // add contact page: handler
    public String addContactView(Model model) {
        ContactForm contactForm = new ContactForm();

        contactForm.setFavorite(false);
        model.addAttribute("contactForm", contactForm);
        return "user/add_contact";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String saveContact(@Valid @ModelAttribute ContactForm contactForm, BindingResult result,
            Authentication authentication, HttpSession session) throws IOException {

        // process the form data

        // 1 validate form

        if (result.hasErrors()) {

            result.getAllErrors().forEach(error -> logger.info(error.toString()));

            session.setAttribute("message", Message.builder()
                    .content("Please correct the following errors")
                    .type(MessageType.red)
                    .build());
            return "user/add_contact";
        }

        String username = Helper.getEmailOfLoggedInUser(authentication);
        // form ---> contact

        User user = userService.getUserByEmail(username);
        // 2 process the contact picture

        // image process

        // uplod karne ka code
        Contact contact = new Contact();
        contact.setName(contactForm.getName());
        contact.setFavorite(contactForm.isFavorite());
        contact.setEmail(contactForm.getEmail());
        contact.setPhoneNumber(contactForm.getPhoneNumber());
        contact.setAddress(contactForm.getAddress());
        contact.setDescription(contactForm.getDescription());
        contact.setUser(user);
        contact.setLinkedInLink(contactForm.getLinkedInLink());
        contact.setWebsiteLink(contactForm.getWebsiteLink());

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            String filename = UUID.randomUUID().toString();
            String fileURL = imageService.uploadImage(contactForm.getContactImage(), filename);
            contact.setPicture(fileURL);
            contact.setCloudinaryImagePublicId(filename);

        }
        contactService.save(contact);
        System.out.println(contactForm);


        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully added a new contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";

    }

    // view contacts

    @RequestMapping
    public String viewContacts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction, Model model,
            Authentication authentication) {

        // load all the user contacts
        String username = Helper.getEmailOfLoggedInUser(authentication);

        User user = userService.getUserByEmail(username);

        Page<Contact> pageContact = contactService.getByUser(user, page, size, sortBy, direction);

        model.addAttribute("pageContact", pageContact);
        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        model.addAttribute("contactSearchForm", new ContactSearchForm());

        return "user/contacts";
    }

    // search handler

    @RequestMapping("/search")
    public String searchHandler(

            @ModelAttribute ContactSearchForm contactSearchForm,
            @RequestParam(value = "size", defaultValue = AppConstants.PAGE_SIZE + "") int size,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "sortBy", defaultValue = "name") String sortBy,
            @RequestParam(value = "direction", defaultValue = "asc") String direction,
            Model model,
            Authentication authentication) {

        logger.info("field {} keyword {}", contactSearchForm.getField(), contactSearchForm.getValue());

        var user = userService.getUserByEmail(Helper.getEmailOfLoggedInUser(authentication));

        Page<Contact> pageContact = null;
        if (contactSearchForm.getField().equalsIgnoreCase("name")) {
            pageContact = contactService.searchByName(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("email")) {
            pageContact = contactService.searchByEmail(contactSearchForm.getValue(), size, page, sortBy, direction,
                    user);
        } else if (contactSearchForm.getField().equalsIgnoreCase("phone")) {
            pageContact = contactService.searchByPhoneNumber(contactSearchForm.getValue(), size, page, sortBy,
                    direction, user);
        }

        logger.info("pageContact {}", pageContact);

        model.addAttribute("contactSearchForm", contactSearchForm);

        model.addAttribute("pageContact", pageContact);

        model.addAttribute("pageSize", AppConstants.PAGE_SIZE);

        return "user/search";
    }

    // detete contact
   @RequestMapping("/delete/{contactId}")
    public String deleteContact(@PathVariable("contactId") String contactId, HttpSession session) {
    try {
        var contact = contactService.getById(contactId);

        if (contact != null) {
            contactService.delete(contactId);

            session.setAttribute("message", Message.builder()
                .content("Contact deleted successfully.")
                .type(MessageType.green)
                .build());
        } else {
            session.setAttribute("message", Message.builder()
                .content("Contact not found or already deleted.")
                .type(MessageType.red)
                .build());
        }
    } catch (Exception e) {
        logger.error("Error deleting contactId {}: {}", contactId, e.getMessage(), e);

        session.setAttribute("message", Message.builder()
            .content("Something went wrong while deleting the contact.")
            .type(MessageType.red)
            .build());
    }

        return "redirect:/user/contacts";
    }


    // update contact form view
    @GetMapping("/view/{contactId}")
    public String updateContactFormView(
            @PathVariable("contactId") String contactId,
            Model model) {

        var contact = contactService.getById(contactId);
        ContactForm contactForm = new ContactForm();
        contactForm.setName(contact.getName());
        contactForm.setEmail(contact.getEmail());
        contactForm.setPhoneNumber(contact.getPhoneNumber());
        contactForm.setAddress(contact.getAddress());
        contactForm.setDescription(contact.getDescription());
        contactForm.setFavorite(contact.isFavorite());
        contactForm.setWebsiteLink(contact.getWebsiteLink());
        contactForm.setLinkedInLink(contact.getLinkedInLink());
        contactForm.setPicture(contact.getPicture());
        ;
        model.addAttribute("contactForm", contactForm);
        model.addAttribute("contactId", contactId);
        

        return "user/update_contact_view";
    }
    //

    @GetMapping("/api/contacts/{contactId}")
    @ResponseBody
    public ResponseEntity<ContactForm> getContactById(@PathVariable String contactId, Authentication authentication) {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userService.getUserByEmail(email);

        var contact = contactService.getById(contactId);

        if (contact == null || !contact.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.SC_NOT_FOUND).build();
        }

        // Convert entity to DTO so you don't leak unnecessary data
        ContactForm dto = new ContactForm();
        dto.setName(contact.getName());
        dto.setEmail(contact.getEmail());
        dto.setPhoneNumber(contact.getPhoneNumber());
        dto.setAddress(contact.getAddress());
        dto.setDescription(contact.getDescription());
        dto.setFavorite(contact.isFavorite());
        dto.setWebsiteLink(contact.getWebsiteLink());
        dto.setLinkedInLink(contact.getLinkedInLink());
        dto.setPicture(contact.getPicture());

        return ResponseEntity.ok(dto);
    }
   

    
    @RequestMapping(value = "/update/{contactId}", method = RequestMethod.POST)
    public String updateContact(@PathVariable("contactId") String contactId,
            @Valid @ModelAttribute ContactForm contactForm,
            BindingResult bindingResult,HttpSession session,
            Model model) throws IOException {

        // update the contact
        if (bindingResult.hasErrors()) {
            return "user/update_contact_view";
        }

        var con = contactService.getById(contactId);
        con.setId(contactId);
        con.setName(contactForm.getName());
        con.setEmail(contactForm.getEmail());
        con.setPhoneNumber(contactForm.getPhoneNumber());
        con.setAddress(contactForm.getAddress());
        con.setDescription(contactForm.getDescription());
        con.setFavorite(contactForm.isFavorite());
        con.setWebsiteLink(contactForm.getWebsiteLink());
        con.setLinkedInLink(contactForm.getLinkedInLink());

        // process image:

        if (contactForm.getContactImage() != null && !contactForm.getContactImage().isEmpty()) {
            logger.info("file is not empty");
            String fileName = UUID.randomUUID().toString();
            String imageUrl = imageService.uploadImage(contactForm.getContactImage(), fileName);
            con.setCloudinaryImagePublicId(fileName);
            con.setPicture(imageUrl);
            contactForm.setPicture(imageUrl);

        } else {
            logger.info("file is empty");
        }

        var updateCon = contactService.update(con);
        logger.info("updated contact {}", updateCon);

        session.setAttribute("message",
                Message.builder()
                        .content("You have successfully updated the contact")
                        .type(MessageType.green)
                        .build());

        return "redirect:/user/contacts";
    }

    @GetMapping("/export")
    public void exportContacts(HttpServletResponse response, Authentication authentication) throws IOException {
    String email = Helper.getEmailOfLoggedInUser(authentication);
    User user = userService.getUserByEmail(email);

    List<Contact> contacts = contactService.getContactsByUser(user);

    // Set CSV response
    response.setContentType("text/csv");
    response.setHeader("Content-Disposition", "attachment; filename=contacts.csv");

    PrintWriter writer = response.getWriter();
    writer.println("Created At,Name,Email,Phone,Address,Description,LinkedIn,Website,Other Link,Favorite");

    for (Contact c : contacts) {
        writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
            c.getCreatedAt(),
            c.getName(),
            c.getEmail(),
            c.getPhoneNumber(),
            c.getAddress(),
            c.getDescription(),
            c.getLinkedInLink(),
            c.getWebsiteLink(),
            c.getOtherLink(),
            c.isFavorite() ? "Yes" : "No"
    );
}
writer.flush();

}

 // Handle category filtered requests
   @GetMapping("/filter")
    public String getContactsByFilter(
        @RequestParam(required = false) String filter,
        Authentication authentication,  
        Model model,
        HttpServletRequest request) {
    
    // Handle unauthenticated access
    if (authentication == null || !authentication.isAuthenticated()) {
        return "redirect:/login?redirect=" + request.getRequestURI();
    }
    
    try {
        String email = Helper.getEmailOfLoggedInUser(authentication);
        User user = userRepo.findByEmail(email)
               .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
        // Set counts for categories
        model.addAttribute("favoriteCount", contactService.countFavoritesByUser(user));
        model.addAttribute("recentCount", contactService.countRecentByUser(user));
        model.addAttribute("linkedinCount", contactService.countWithLinkedInByUser(user));
        model.addAttribute("websiteCount", contactService.countWithWebsiteByUser(user));
        model.addAttribute("missingEmailCount", contactService.countMissingEmailByUser(user));
        model.addAttribute("missingAddressCount", contactService.countMissingAddressByUser(user));
        model.addAttribute("loggedInUser", user);
        
        // Set contacts based on filter
        switch (filter != null ? filter : "") {
            case "favorites":
                model.addAttribute("contacts", contactService.findFavoritesByUser(user));
                model.addAttribute("filterTitle", "Favorite Contacts");
                break;
            case "recent":
                model.addAttribute("contacts", contactService.findRecentByUser(user));
                model.addAttribute("filterTitle", "Recently Added Contacts");
                break;
            case "linkedin":
                model.addAttribute("contacts", contactService.findWithLinkedInByUser(user));
                model.addAttribute("filterTitle", "Contacts With LinkedIn");
                break;
            case "website":
                model.addAttribute("contacts", contactService.findWithWebsiteByUser(user));
                model.addAttribute("filterTitle", "Contacts With Website");
                break;
            case "missingEmail":
                model.addAttribute("contacts", contactService.findMissingEmailByUser(user));
                model.addAttribute("filterTitle", "Contacts Missing Email");
                break;
            case "missingAddress":
                model.addAttribute("contacts", contactService.findMissingAddressByUser(user));
                model.addAttribute("filterTitle", "Contacts Missing Address");
                break;
            default:
                model.addAttribute("contacts", contactService.findAllByUser(user));
                model.addAttribute("filterTitle", "All Contacts");
        }
        
        return "user/list";
        
    } catch (UsernameNotFoundException e) {
        return "redirect:/login?error=user_not_found";
    }

    
}
}