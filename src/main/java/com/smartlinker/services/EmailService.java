package com.smartlinker.services;

import com.smartlinker.forms.ContactForm;

public interface EmailService {

    void sendEmail(String to, String subject, String body);

    void sendEmailWithHtml();
  
    void sendEmailWithAttachment();

    //send email service method for contact us page
    void sendContactMail(ContactForm contactForm);

}
