package com.example.suivreapp.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired(required = false) // Allow testing without email setup
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String supportEmail;


    public void sendPasswordResetEmail(String to, String resetLink) throws MessagingException {
        if (mailSender == null) {
            System.out.println("Email not configured. Reset Link: " + resetLink);
            return;
        }

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // Specify encoding
        helper.setTo(to);
        helper.setSubject("Réinitialisation de votre mot de passe");
        helper.setText(
            "<h3>Réinitialisation de mot de passe</h3>" +
            "<p>Vous avez demandé à réinitialiser votre mot de passe. Cliquez sur le lien ci-dessous pour procéder :</p>" +
            "<a href=\"" + resetLink + "\">Réinitialiser mon mot de passe</a>" +
            "<p>Ce lien expire dans 24 heures.</p>" +
            "<p>Si vous n'avez pas fait cette demande, ignorez cet e-mail.</p>",
            true
        );
        mailSender.send(message);
    }
    public void sendContactEmail(String name, String email, String message) throws MessagingException {
        if (mailSender == null) {
            System.out.println("Email not configured. Contact Message: " + message);
            return;
        }

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(supportEmail); // Send to SUPPORT_EMAIL (ssilinee19@gmail.com)
        helper.setSubject("New Contact Us Message from " + name);
        helper.setText(
            "<h3>New Contact Us Message</h3>" +
            "<p><strong>Name:</strong> " + name + "</p>" +
            "<p><strong>Email:</strong> " + email + "</p>" +
            "<p><strong>Message:</strong> " + message + "</p>",
            true
        );
        helper.setFrom(email); // Set the sender as the user's email

        mailSender.send(mimeMessage);
    }


	
}