package com.example.suivreapp.service;



import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.suivreapp.model.AuthResponse;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.LoginRequest;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.User;
import com.example.suivreapp.model.UserDTO;
import com.example.suivreapp.model.VerifyUserDto;
import com.example.suivreapp.repository.DoctorRepository;
import com.example.suivreapp.repository.PatientRepository;
import com.example.suivreapp.repository.UserRepository;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;


@Service
public class AuthService {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;

   

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    

    public String forgotPassword(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (!userOptional.isPresent()) {
            return "Utilisateur non trouvé avec l'e-mail fourni.";
        }

        String token = jwtService.generateResetToken(email); // Delegate to JwtService
        String resetLink = "http://192.168.1.8:4200/reset-password?token=" + token;
        try {
            emailService.sendPasswordResetEmail(email, resetLink);
        } catch (MessagingException e) {
            return "Erreur lors de l'envoi de l'e-mail de réinitialisation.";
        }

        return "Lien de réinitialisation envoyé à votre e-mail.";
    }

    public String resetPassword(String token, String newPassword) {
        try {
            String email = jwtService.extractEmailFromResetToken(token); // Delegate to JwtService
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                return "Utilisateur non trouvé.";
            }

            User user = userOptional.get();
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "Mot de passe réinitialisé avec succès.";
        } catch (JwtException e) {
            return "Jeton invalide ou expiré.";
        }
    }
    


    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));

        // Check password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        // Authenticate the user
        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), loginRequest.getPassword()));

        // Generate JWT Token
        String token = jwtService.generateAuthToken(user);

        
        Long patientId = null;
        if ("PATIENT".equals(user.getRole())) {
        	Patient patient  = patientRepository.findByEmail(user.getEmail()).orElse(null);
        	if (patient != null) {
        	    patientId = patient.getId(); // Get patient_id
        	
            }
        }
        // Retrieve doctor ID if the user is a doctor
        Long doctorId = null;
        if ("doctor".equals(user.getRole())) {
        	Doctor doctor = doctorRepository.findByEmail(user.getEmail()).orElse(null);
        	if (doctor != null) {
        	    doctorId = doctor.getId(); // Get doctor_id
        	
            }
        }
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

        // Now create the AuthResponse and return it
        return new AuthResponse(user.getId(),patientId, doctorId, token, user.getRole(), userDTO);

    }


    public User authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found")); // Handle Optional

        // Compare hashed password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return user;
    }

    
   
}
