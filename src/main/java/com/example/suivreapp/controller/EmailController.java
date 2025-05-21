package com.example.suivreapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.suivreapp.model.ContactFotmDTO;
import com.example.suivreapp.service.EmailService;

import jakarta.mail.MessagingException;



    @RestController
    @RequestMapping("/api/email")
    public class EmailController {

        @Autowired
        private EmailService emailService;

        @PostMapping("/contact/send-email")
        public ResponseEntity<String> sendContactEmail(@RequestBody ContactFotmDTO contactForm) {
            try {
                emailService.sendContactEmail(contactForm.getName(), contactForm.getEmail(), contactForm.getMessage());
                return ResponseEntity.ok("Email sent successfully");
            } catch (MessagingException e) {
                return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
            }
        }
        }
    

