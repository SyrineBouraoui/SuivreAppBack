package com.example.suivreapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.suivreapp.service.EmailService;

import jakarta.mail.MessagingException;



    @RestController
    @RequestMapping("/api/email")
    public class EmailController {

        @Autowired
        private EmailService emailService;

        @PostMapping("/send")
        public String sendEmail(@RequestParam String toEmail,
                                @RequestParam String subject,
                                @RequestParam String message) {
            try {
                emailService.sendVerificationEmail(toEmail, subject, message);
                return "Email sent successfully!";
            } catch (MessagingException e) {
                return "Failed to send email: " + e.getMessage();
            }
        }
    }

