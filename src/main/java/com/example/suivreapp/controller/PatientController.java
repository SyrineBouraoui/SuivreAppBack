package com.example.suivreapp.controller;

import java.lang.System.Logger;
import java.nio.file.AccessDeniedException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.suivreapp.config.CustomUserDetails;
import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.model.PatientDTO;
import com.example.suivreapp.model.User;
import com.example.suivreapp.repository.DoctorRepository;
import com.example.suivreapp.repository.PatientRepository;
import com.example.suivreapp.repository.UserRepository;
import com.example.suivreapp.service.JwtService;
import com.example.suivreapp.service.PatientService;

import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:4200")

public class PatientController {

	@Autowired

	private PatientRepository patientRepository;

	@Autowired

	private DoctorRepository doctorRepository;
	@Autowired
	private PatientService patientService;
	
	@Autowired

	private UserRepository userRepository;
	
	@Autowired
	private JwtService jwtService;

	@GetMapping
	public List<PatientDTO> getAllPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream()
            .map(patient -> new PatientDTO(
                patient.getId(),
                patient.getName(),
                patient.getEmail(),
                patient.getUser() != null ? patient.getUser().getId() : null,
                patient.getDoctor() != null ? patient.getDoctor().getId() : null
            ))
            .collect(Collectors.toList());
    }
	
	@GetMapping("/{patientId}/impersonate")
	public ResponseEntity<?> impersonatePatient(
	    @PathVariable Long patientId, 
	    @RequestHeader(value = "doctorId", required = false) Long doctorId, 
	    @AuthenticationPrincipal CustomUserDetails doctorDetails) {

	    System.out.println("doctorId from request headers: " + doctorId); // Print doctorId received in the request header
	    System.out.println("patientId from request path: " + patientId); // Print patientId received in the URL

	    // Check if the doctorDetails is null (i.e., the doctor is not authenticated)
	    if (doctorDetails == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
	    }

	    // Retrieve the authenticated doctor
	    User doctorUser = doctorDetails.getUser();
	    System.out.println("Authenticated Doctor: " + doctorUser.getUsername() + " (ID: " + doctorUser.getId() + ")");

	    // Fetch the correct doctor entity from the doctorRepository
	    Doctor doctorEntity = doctorRepository.findByUserId(doctorUser.getId());
	    Long doctorIdFromRepo = (doctorEntity != null) ? doctorEntity.getId() : null;

	    if (doctorIdFromRepo == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Doctor not found");
	    }

	    // Print the actual doctorId retrieved from the database
	    System.out.println("Doctor ID from repository: " + doctorIdFromRepo);

	    // If no doctorId is provided in the headers, use the one from the authenticated doctor
	    if (doctorId == null) {
	        doctorId = doctorIdFromRepo; // Use the doctorId fetched from the database
	    }

	    // Now you can check if the `doctorId` passed via the headers matches the logged-in doctor
	    if (!doctorId.equals(doctorIdFromRepo)) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access to this patient");
	    }

	    // Fetch the patient using the patientRepository with both doctorId and patientId
	    Optional<Patient> optionalPatient = patientRepository.findPatientByDoctorIdAndPatientId(doctorIdFromRepo, patientId);

	    if (optionalPatient.isEmpty()) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Patient not found or unauthorized");
	    }

	    // If a patient is found, retrieve and return the patient data (token generation)
	    Patient patient = optionalPatient.get();
	    System.out.println("Impersonating patient: " + patient.getName() + " (ID: " + patient.getId() + ")");

	    //String token = jwtService.generateAuthToken(patient);

	    // Create a response object to include both the token and the patient's name
	    Map<String, Object> response = new HashMap<>();
	    //response.put("token", token);
	    response.put("patientName", patient.getName()); // Include patient's name in the response

	    // Return the response with the token and patient name
	    return ResponseEntity.ok(response);
	}


	// Get patient by id
	@GetMapping("/doctor/{doctorId}")
	public ResponseEntity<List<PatientResponse>> getPatientsByDoctorId(@PathVariable("doctorId") Long doctorId,
			@RequestHeader("Authorization") String authorizationHeader) throws AccessDeniedException {

		// Extract the JWT token from the Authorization header
		String token = authorizationHeader.substring(7); // Removing "Bearer " part

		// Validate the token (You can add your JWT validation logic here)
		if (isTokenValid(token)) {
			List<Patient> patients = patientService.getPatientsByDoctorId(doctorId);

			// Convert the Patient list into a clean response format
			List<PatientResponse> response = patients.stream()
					.map(patient -> new PatientResponse(patient.getId(), patient.getName())) // Modify based on the
																								// fields you need
					.collect(Collectors.toList());

			return ResponseEntity.ok(response); // Return the structured list of patients
		} else {
			throw new AccessDeniedException("Unauthorized access.");
		}
	}

	private boolean isTokenValid(String token) {
		// Validate the token here (Check expiry, signature, and roles)
		return true; // Implement your >validation logic
	}

	// Response DTO class to structure the response data
	public static class PatientResponse {
		private Long id;
		private String name;

		// Constructor
		public PatientResponse(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		// Getters and setters
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	// Add a new patient and associate it with a doctor
	@PostMapping("/register")
	public ResponseEntity<Patient> registerPatient(@RequestBody Patient patient, @RequestParam Long doctorId) {
		// Call the service method to register the patient and associate it with a
		// doctor
		Patient savedPatient = patientService.registerPatient(patient, doctorId);
		return ResponseEntity.status(HttpStatus.CREATED).body(savedPatient);
	}

	// Update a patient
	@PutMapping("/{id}")
	@Transactional
	public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
		if (!patientRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found");
		}

		// Fetch and update the patient
		Patient existingPatient = patientRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));

		existingPatient.setName(patient.getName()); // Example update
		existingPatient.setEmail(patient.getEmail()); // Example update
		// Other fields...

		return patientRepository.save(existingPatient);
	}

	// Delete a patient
	@DeleteMapping("/{id}")
	public void deletePatient(@PathVariable Long id) {
		patientRepository.deleteById(id);
	}
	@GetMapping("/patients/{id}")
	public ResponseEntity<?> getPatientById(@PathVariable Long id) {
	    Optional<Patient> patient = patientRepository.findById(id);
	    if (patient.isPresent()) {
	        return ResponseEntity.ok(patient.get());  // Ensure this returns patient data (not doctor data)
	    }
	    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Patient not found");
	}

}
