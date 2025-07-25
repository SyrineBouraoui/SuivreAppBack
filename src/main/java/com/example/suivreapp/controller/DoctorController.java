package com.example.suivreapp.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.suivreapp.controller.PatientController.PatientResponse;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.DoctorDTO;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.User;
import com.example.suivreapp.model.UserDTO;
import com.example.suivreapp.repository.DoctorRepository;
import com.example.suivreapp.repository.PatientRepository;
import com.example.suivreapp.repository.UserRepository;
import com.example.suivreapp.service.DoctorService;
import com.example.suivreapp.service.JwtService;
import com.example.suivreapp.service.PatientService;
import com.example.suivreapp.model.DoctorDashboard; // Import the DoctorDashboard class
import com.example.suivreapp.model.DoctorResponse;

import ch.qos.logback.core.joran.spi.HttpUtil.RequestMethod;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:4200")// Allows Angular to call this API
public class DoctorController {

    @Autowired
    
    private DoctorRepository doctorRepository;
    @Autowired

	private UserRepository userRepository;


    @Autowired
    private DoctorService doctorService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;  // Autowire the PatientRepository

	@Autowired
	private JwtService jwtService;
    
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id); // Assuming this method returns a Doctor entity
        User user = doctor.getUser();  // Getting associated user

        // Mapping the entity to DTO
        DoctorDTO doctorDTO = new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getEmail());
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());

        // Constructing a custom response with the DTOs
        DoctorResponse doctorResponse = new DoctorResponse(doctorDTO, userDTO);

        return ResponseEntity.ok(doctorResponse);
    }

    @GetMapping("/getDoctor/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);  // Get doctor from the database
        System.out.println("Doctor fetched: " + doctor);  // Log doctor to debug

        if (doctor != null) {
            DoctorDTO doctorDTO = new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getEmail());
            System.out.println("DoctorDTO created: " + doctorDTO);  // Log DoctorDTO

            // Get user associated with the doctor
            User user = doctor.getUser();  
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
                .map(patient -> new PatientResponse(patient.getId(), patient.getName()))
                .collect(Collectors.toList());

            System.out.println("PatientResponses: " + patientResponses);  // Log the PatientResponses

            // Wrap doctor DTO, user DTO, and patients into DoctorResponse
            DoctorResponse doctorResponse = new DoctorResponse(doctorDTO, userDTO);
            doctorResponse.setPatients(null) ; // Add patients to the response

            System.out.println("DoctorResponse: " + doctorResponse);  // Log the final response

            return ResponseEntity.ok(doctorResponse);  // Return the DoctorResponse
        } else {
            return ResponseEntity.noContent().build();  // Return 204 if no doctor found
        }
    }


    
    
    
    
    
    

    @GetMapping
    public List<DoctorDTO> getAllDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
            .map(doctor -> new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getEmail()))
            .collect(Collectors.toList());
    }
  

    // Add a new doctor
    @PostMapping("/register")
	public ResponseEntity<Doctor> registerDoctor(@RequestBody Doctor doctor) {
	    Doctor savedDoctor = doctorService.registerDoctor(doctor);
	    return ResponseEntity.ok(savedDoctor);}
    @PutMapping("/{id}")
    public ResponseEntity<DoctorDTO> updateDoctor(@PathVariable Long id, @RequestBody DoctorDTO doctorDTO) {
        Optional<Doctor> existingDoctor = doctorRepository.findById(id);
        if (existingDoctor.isPresent()) {
            Doctor doctor = existingDoctor.get();
            doctor.setName(doctorDTO.getName());
            doctor.setEmail(doctorDTO.getEmail());
            Doctor updatedDoctor = doctorRepository.save(doctor);
            // Map the updated Doctor back to DoctorDTO
            DoctorDTO responseDTO = new DoctorDTO(updatedDoctor.getId(), updatedDoctor.getName(), updatedDoctor.getEmail());
            return ResponseEntity.ok(responseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    // Delete a doctor
    @DeleteMapping("/{id}")
    public void deleteDoctor(@PathVariable Long id) {
        doctorRepository.deleteById(id);
    }

    // Get patients for a specific doctor
    @GetMapping("/{id}/patients")
    public List<Patient> getPatientsForDoctor(@PathVariable Long id) {
        // Assuming Patient has a doctorId or doctor object as a relation
        return patientRepository.findByDoctorId(id);  // This should return the list of patients for that doctor
    }
}
