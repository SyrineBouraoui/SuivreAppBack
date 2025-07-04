package com.example.suivreapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.suivreapp.controller.PatientController.PatientResponse;
import com.example.suivreapp.model.AuthResponse;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.DoctorDTO;
import com.example.suivreapp.model.DoctorResponse;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.User;
import com.example.suivreapp.model.Role;

import com.example.suivreapp.model.UserDTO;
import com.example.suivreapp.model.UserResponse;
import com.example.suivreapp.model.UserUpdateRequest;
import com.example.suivreapp.repository.UserRepository;
import com.example.suivreapp.service.PatientService;
import com.example.suivreapp.service.UserService;

import jakarta.transaction.Transactional;

import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200") // Allows Angular to call this API
public class UserController {


	
	
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

	
    @Autowired
    private PatientService patientService;
    
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        System.out.println("Received User Role: " + user.getRole());  // Log received user role
        boolean isDoctor =user.getRole().equals("DOCTOR");

        System.out.println("Is Doctor: " + isDoctor);  // Log the result of the check

        User savedUser = userService.registerUser(user, isDoctor);
        return ResponseEntity.ok(savedUser);
    }





	 @GetMapping("/{id}")
	    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
	        User user = userRepository.findById(id).orElse(null);
	        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
	        UserResponse userResponse = new UserResponse(userDTO);

	        if (user == null) {
	            return ResponseEntity.notFound().build();
	        }
	        return ResponseEntity.ok(userResponse);
	    }
	 
	 @PutMapping("/{id}")
	    @Transactional
	    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest updateRequest) {
	        if (!userRepository.existsById(id)) {
	            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id);
	        }

	        try {
	            // Fetch and update the user
	            User existingUser = userRepository.findById(id)
	                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));

	            existingUser.setUsername(updateRequest.getUsername());
	            existingUser.setEmail(updateRequest.getEmail());
	            existingUser.setRole(Role.valueOf(updateRequest.getRole().toUpperCase()));
	            existingUser.setDoctorId(updateRequest.getDoctorId());

	            // Save updated user
	            User updatedUser = userRepository.save(existingUser);

	            // Map to DTO for response
	            UserDTO userDTO = new UserDTO(
	                    updatedUser.getId(),
	                    updatedUser.getUsername(),
	                    updatedUser.getEmail(),
	                    updatedUser.getRole()
	            );
	            UserResponse userResponse = new UserResponse(userDTO);
	            return ResponseEntity.ok(userResponse);
	        } catch (IllegalArgumentException e) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid role: " + updateRequest.getRole());
	        }
	    }

	    // Delete a doctor
	    @DeleteMapping("/{id}")
	    public void deleteDoctor(@PathVariable Long id) {
	        userRepository.deleteById(id);
	    }

	    @GetMapping
	    public ResponseEntity<UserResponse> getAllUsers() {
	        List<User> users = userService.getAllUsers();  // Get all users from the database
	        
	        if (users != null && !users.isEmpty()) {
	            // Convert all users into UserDTO
	            List<UserDTO> userDTOList = new ArrayList<>();
	            for (User user : users) {
	                UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
	                userDTOList.add(userDTO);
	            }

	            UserResponse userResponse = new UserResponse(userDTOList);  // Wrap it in UserResponse
	            return ResponseEntity.ok(userResponse);  // Return response with user DTOs
	        } else {
	            return ResponseEntity.noContent().build();  // Return 204 if no users found
	        }
	    }
	    
	    @GetMapping("/getUser/{id}")
	    public ResponseEntity<UserResponse> getConnectedUserWithPatients(@PathVariable Long id) {
	        User user = userService.getUserById(id);  // Get user from the database
	        System.out.println("User fetched: " + user);  // Log user to debug

	        if (user != null) {
	            // Convert user into UserDTO
	            UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
	            System.out.println("UserDTO created: " + userDTO);  // Log UserDTO

	            // Get patients for this doctor
	            List<Patient> patients = patientService.getPatientsByDoctorId(id);  
	            System.out.println("Patients fetched: " + patients);  // Log the list of patients

	            if (patients.isEmpty()) {
	                System.out.println("No patients found for doctor with ID: " + id);
	            } else {
	                System.out.println("Patients found: " + patients.size());
	            }

	            // Map patients to PatientResponse
	            List<PatientResponse> patientResponses = patients.stream()
	                .map(patient -> new PatientResponse(patient.getId(), patient.getName()))  // Map patients to PatientResponse
	                .collect(Collectors.toList());
	            
	            System.out.println("PatientResponses: " + patientResponses);  // Log the PatientResponses

	            // Wrap the user DTO and the list of patients into a DoctorResponse
	            UserResponse userResponse = new UserResponse(userDTO);
	            userResponse.setPatients(patientResponses);  // Add patients to the response

	            System.out.println("UserResponse: " + userResponse);  // Log the final response

	            return ResponseEntity.ok(userResponse);  // Return the UserResponse with user and patients
	        } else {
	            return ResponseEntity.noContent().build();  // Return 204 if no user found
	        }
	    }  
	           

	    @GetMapping("/details")
	    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
	        if (userDetails == null) {
	            return ResponseEntity.ok(Collections.singletonMap("user", null));
	        }

	        // Fetch the user based on the authenticated user's username
	        User user = userService.findByUsername(userDetails.getUsername());
	        if (user == null) {
	            return ResponseEntity.ok(Collections.singletonMap("user", null));
	        }

	        // Create UserDTO from the User entity
	        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

	        // Initialize DoctorDTO as null
	        DoctorDTO doctorDTO = null;

	        // Check if the user has a doctor profile
	        if (user.getRole().equals("doctor")) {
	            Doctor doctor = user.getDoctor();  // Assuming user has a Doctor associated with it
	            if (doctor != null) {
	                // If doctor exists, create DoctorDTO from Doctor entity
	                doctorDTO = new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getEmail());
	            }
	        }

	        // Return a DoctorResponse containing both DoctorDTO and UserDTO
	        DoctorResponse doctorResponse = new DoctorResponse(doctorDTO, userDTO);
	        return ResponseEntity.ok(Collections.singletonMap("user", doctorResponse));
	    }
}