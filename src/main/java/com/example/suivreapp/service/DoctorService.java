package com.example.suivreapp.service;

    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.Optional;

    import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import com.example.suivreapp.model.Doctor;
    import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.User;
import com.example.suivreapp.repository.DoctorRepository;
    import com.example.suivreapp.repository.PatientRepository;

    @Service
    public class DoctorService {
        @Autowired
        
        private DoctorRepository doctorRepo;

        
        private final PasswordEncoder passwordEncoder = null;
        @Autowired
        private PatientRepository patientRepo;

        public Map<String, Object> getDoctorDashboard(Long doctorId) {
            Doctor doctor = doctorRepo.findById(doctorId).orElse(null);
            List<Patient> patients = patientRepo.findByDoctorId(doctorId);

            Map<String, Object> response = new HashMap();
            response.put("doctorName", doctor.getName());
            response.put("patients", patients);
            return response;
        }


        public Optional<Doctor> findByEmail(String email) {
            return doctorRepo.findByEmail(email);
        }

        public Doctor registerDoctor(Doctor doctor) {
            // Hash the password before saving it
            doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
            return doctorRepo.save(doctor);
        }
    
        public Doctor getDoctorById(Long id) {
            return doctorRepo.findById(id).orElse(null); // Return null if user not found
        }
        public Doctor getUserById(Long id) {
            System.out.println("Fetching user with ID: " + id);  // Add this log to verify if the ID is correct
            Optional<Doctor> user = doctorRepo.findById(id);
            if (user.isPresent()) {
                System.out.println("User found: " + user.get());
                return user.get();
            } else {
                System.out.println("User with ID " + id + " not found.");
                return null;
            }
        }

        
       
        
       public Doctor findDoctorByUserId(Long userId) {
           // Assuming you have a method in your repository to find a doctor by userId
           return doctorRepo.findByUserId(userId);
       }
   }
    
    
    
