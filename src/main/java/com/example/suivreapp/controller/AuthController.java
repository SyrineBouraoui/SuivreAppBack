package com.example.suivreapp.controller;

import com.example.suivreapp.model.LoginRequest;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.ResetPasswordRequest;
import com.example.suivreapp.model.Role;
import com.example.suivreapp.model.SignupRequest;
import com.example.suivreapp.model.User;
import com.example.suivreapp.model.UserDTO;
import com.example.suivreapp.model.VerifyUserDto;
import com.example.suivreapp.repository.DoctorRepository;
import com.example.suivreapp.repository.PatientRepository;
import com.example.suivreapp.repository.UserRepository;
import com.example.suivreapp.model.AuthResponse;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.ForgotPasswordRequest;
import com.example.suivreapp.model.JwtResponse;
import com.example.suivreapp.service.AuthService; // Service to handle authentication logic
import com.example.suivreapp.service.EmailService;
import com.example.suivreapp.service.JwtService;
import com.example.suivreapp.service.UserService;


import org.json.JSONObject;

import io.jsonwebtoken.JwtException;
import jakarta.mail.MessagingException;

import java.security.Provider.Service;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;




@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private UserService userService;


    @Autowired
    private EmailService emailService;

    
    @Autowired
    private JwtService JwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private DoctorRepository doctorRepository; // Inject repository

    @Autowired
    private PatientRepository patientRepository; // Inject repository

    @Autowired
    private UserRepository userRepository; // Inject repository
    @Autowired
    private AuthService authService;

    
    
    
    
    
    
    
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody @Validated SignupRequest signupRequest) {
        System.out.println("Received Signup Request: " + signupRequest.getEmail());  // Add logging

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }
        Role role = Role.valueOf(signupRequest.getRole().toUpperCase());  // Convert the role to enum

        // Create a new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setRole(role);

        // Handling Doctor Role
        if ("doctor".equalsIgnoreCase(signupRequest.getRole())) {
            Doctor doctor = new Doctor();
            doctor.setName(signupRequest.getUsername());
            doctor.setEmail(signupRequest.getEmail());
            doctor.setPassword(user.getPassword());
            doctor.setUser(user); // Link with User

            user.setDoctor(doctor); // Link doctor to User

            System.out.println("Saving user: " + user);
            userRepository.save(user);

            doctorRepository.save(doctor);

        } 
        // Handling Patient Role
        else if ("patient".equalsIgnoreCase(signupRequest.getRole())) {
            Patient patient = new Patient();
            patient.setName(signupRequest.getUsername());
            patient.setEmail(signupRequest.getEmail());
            patient.setPassword(user.getPassword());
            patient.setUser(user); // Link with User

            // Optionally link to a doctor
            if (signupRequest.getId() != null) {
                Doctor doctor = doctorRepository.findById(signupRequest.getId()).orElse(null);
                if (doctor != null) {
                    patient.setDoctor(doctor); // Link patient to doctor
                }
            }

            user.setPatient(patient); // Link patient to User

            System.out.println("Saving user: " + user);
            userRepository.save(user);

            patientRepository.save(patient);
        } 
        else if ("admin".equalsIgnoreCase(signupRequest.getRole())) {
            // ADMIN has no additional entity to create (no doctor or patient)
            userRepository.save(user);
        }

        else {
            return ResponseEntity.badRequest().body("Error: Invalid role!");
        }

        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("Received Login Request: " + loginRequest.getEmail());

        try {
            // Authenticate user with email and password
            User user = authService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

            // Debugging: Log user details before generating the token
            System.out.println("Generating token for user: " + user.getUsername());
            System.out.println("User ID: " + user.getId());  // Log user ID to confirm it's valid

            // Generate JWT Token for the authenticated user
            String token = JwtService.generateAuthToken(user);
            System.out.println("Generated Token Length: " + token.length());

            // Debugging: Log the generated token
            System.out.println("Generated Token: " + token); // <-- This line is crucial for debugging

            Long patientId = null;
            Long doctorId = null;

            // Check if the user is a PATIENT and fetch the associated patientId
            if (user.getRole() == Role.PATIENT && user.getPatient() != null) {
                patientId = user.getPatient().getId();  // Fetch the patientId from the Patient entity
                if (user.getPatient().getDoctor() != null) {
                    doctorId = user.getPatient().getDoctor().getId();  // Fetch the doctorId from the Doctor entity associated with the patient
                }
            }

            // Check if the user is a DOCTOR and fetch the associated doctorId
            if (user.getRole() == Role.DOCTOR && user.getDoctor() != null) {
                doctorId = user.getDoctor().getId();  // Fetch the doctorId from the Doctor entity
            }

            // Create a UserDTO to return user details without exposing sensitive information
            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

            // Create AuthResponse that includes token, user details, doctorId, and patientId
            AuthResponse authResponse = new AuthResponse(user.getId(), patientId, doctorId, token, user.getRole(), userDTO);

            // Return the AuthResponse with the token and user details
            return ResponseEntity.ok(authResponse);

        } catch (Exception e) {
            // Log the exception and return a response with Unauthorized status
            System.out.println("Error during authentication: " + e.getMessage()); // <-- This will show the error in the logs
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    
    
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        System.out.println(">>> Forgot password endpoint hit for: " + request.getEmail());

        Optional<User> userOptional = userRepository.findByEmail(request.getEmail());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé avec l'e-mail fourni.");
        }

        String token = JwtService.generateResetToken(request.getEmail());
        String resetLink = "http://localhost:4200/forgot-password?token=" + token;

        try {
            emailService.sendPasswordResetEmail(request.getEmail(), resetLink);
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de l'envoi de l'e-mail.");
        }

        return ResponseEntity.ok("Lien de réinitialisation envoyé.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getNewPassword(); 

        try {
            // Extract email from token
            String email = extractEmailFromJwt(token);

            // Find the user by email
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

            // Encode the new password
            String encodedPassword = passwordEncoder.encode(newPassword);

            // Update and save the user
            user.setPassword(encodedPassword);
            userRepository.save(user);

            // Return a JSON response with a "message" field
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful");
            return ResponseEntity.ok(response);  // Returning JSON response

        } catch (Exception e) {
            // Return error as JSON too
            Map<String, String> errorResponse = new HashMap();
            errorResponse.put("error", "Error resetting password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    

    public String extractEmailFromJwt(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) throw new IllegalArgumentException("Invalid JWT token");

        String payloadJson = new String(Base64.getDecoder().decode(parts[1]));

        System.out.println("Decoded Payload: " + payloadJson);

        // Extract "sub" field (email) using regex
        String email = payloadJson.replaceAll(".*\"sub\":\"([^\"]+)\".*", "$1");
        return email;
    }


    
   }