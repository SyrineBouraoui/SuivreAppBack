package com.example.suivreapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.repository.DoctorRepository;
import com.example.suivreapp.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    
    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }
    
;


    public List<Patient> findByDoctorId(Long doctorId) {
        return patientRepository.findByDoctorId(doctorId);
    }

    

    // Register a patient and associate it with a doctor
    public Patient registerPatient(Patient patient, Long doctorId) {
        // Find the doctor by ID
        Doctor doctor = doctorRepository.findById(doctorId)
            .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Assign the doctor to the patient
        patient.setDoctor(doctor);

        // Save the patient and return the saved entity
        return patientRepository.save(patient);
    }

    public Optional<Patient> getPatientByIdAndDoctor(Long patientId, Doctor doctor) {
        return patientRepository.findByIdAndDoctor(patientId, doctor);
    }

    public List<Patient> getPatientsByDoctorId(Long doctorId) {
        System.out.println("Fetching patients for doctor with ID123: " + doctorId);  // Debug log
        return patientRepository.findByDoctorId(doctorId);
    }

     
}
