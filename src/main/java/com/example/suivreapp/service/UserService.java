package com.example.suivreapp.service;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.suivreapp.config.CustomUserDetails;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.Role;
import com.example.suivreapp.model.User;
import com.example.suivreapp.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
public class UserService implements UserDetailsService {
	
	@Autowired
    private  UserRepository userRepository;
	
	@Autowired
	private DoctorService doctorService;
	@Autowired
    private  PasswordEncoder passwordEncoder;  // Inject PasswordEncoder


    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from the database
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Return a CustomUserDetails instance
        return new CustomUserDetails(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(User user, boolean isDoctor) {
        System.out.println("Registering user: " + user.getUsername());

        // Set the role properly
        if (isDoctor) {
            user.setRole(Role.DOCTOR);  // Set the role as DOCTOR
        } else {
            user.setRole(Role.PATIENT);  // Or set other roles as needed
        }

        // Create and set doctor
        if (isDoctor) {
            Doctor doctor = new Doctor();
            doctor.setName(user.getUsername());
            doctor.setEmail(user.getEmail());
            doctor.setPassword(user.getPassword());

            // Set the doctor and user relationship
            doctor.setUser(user);  // Set user for doctor
            user.setDoctor(doctor); // Set doctor for user

            // Set doctor_id manually in the User entity
            user.setDoctorId(doctor.getId()); // Set doctor_id manually
            System.out.println("Doctor ID: " + user.getDoctorId());  // Log doctor_id for confirmation
        }

        // Save and return user
        User savedUser = userRepository.save(user);
        return savedUser;
    }






    public User getUserById(Long id) {
        System.out.println("Fetching user with ID: " + id);  // Add this log to verify if the ID is correct
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            System.out.println("User found: " + user.get());
            return user.get();
        } else {
            System.out.println("User with ID " + id + " not found.");
            return null;
        }
    }
   

    // Method to update a user
    public User updateUser(User user) {
        return userRepository.save(user); // Save the updated user and return the result
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch and return all users from the repository
    }

    
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

}
