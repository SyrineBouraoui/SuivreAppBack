package com.example.suivreapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.suivreapp.model.Doctor;
import com.example.suivreapp.model.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
	@Query("SELECT p FROM Patient p WHERE p.doctor.id = :doctorId")
	List<Patient> findByDoctorId(@Param("doctorId") Long doctorId);

    Optional<Patient> findByEmail(String email);
    boolean existsByEmail(String email); 

    Optional<Patient> findByIdAndDoctor(Long id, Doctor doctor);
    @Query("SELECT p FROM Patient p WHERE p.doctor.id = :doctorId AND p.id = :patientId")
    Optional<Patient> findPatientByDoctorIdAndPatientId(@Param("doctorId") Long doctorId, @Param("patientId") Long patientId);

    Optional<Patient> findById(Long id);

}

