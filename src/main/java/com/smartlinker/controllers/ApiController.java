package com.smartlinker.controllers;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartlinker.entities.Contact;
import com.smartlinker.entities.User;
import com.smartlinker.services.ContactService;
import com.smartlinker.services.UserService;

@RestController
@RequestMapping("/api")
public class ApiController {

    // get contact

    @Autowired
    private ContactService contactService;

    @GetMapping("/contacts/{contactId}")
    public Contact getContact(@PathVariable String contactId) {
        return contactService.getById(contactId);
    }

    //get user

    @Autowired
    private UserService userService;

    @GetMapping("/user/{userId}")
    public Optional<User> getUser(@PathVariable String userId) {
        return userService.getUserById(userId);
    }


}
