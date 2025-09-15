package com.smartlinker.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.smartlinker.forms.ContactForm;
import com.smartlinker.services.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender emailSender;

    // Inject the email domain (your sender email) from application.properties
    @Value("${spring.mail.properties.domain_name}")
    private String senderEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(senderEmail);

        emailSender.send(message);
        System.out.println("Email was sent successfully!");
    }

    @Override
    public void sendContactMail(ContactForm contactForm) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("arvindwork16@gmail.com");
        message.setSubject("New Contact Form Submission from " + contactForm.getName());

        // Construct the email body
        StringBuilder body = new StringBuilder();
        body.append("You've received a new message:\n\n");
        body.append("Name: ").append(contactForm.getName()).append("\n");
        body.append("Email: ").append(contactForm.getEmail()).append("\n");
        body.append("Phone: ").append(contactForm.getPhoneNumber()).append("\n");
        body.append("Message:\n").append(contactForm.getDescription());

        message.setText(body.toString());
        message.setFrom(senderEmail);

        emailSender.send(message);
        System.out.println("Contact form email sent successfully!");
    }

    @Override
    public void sendEmailWithHtml() {
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithHtml'");
    }

    @Override
    public void sendEmailWithAttachment() {
        throw new UnsupportedOperationException("Unimplemented method 'sendEmailWithAttachment'");
    }
}
