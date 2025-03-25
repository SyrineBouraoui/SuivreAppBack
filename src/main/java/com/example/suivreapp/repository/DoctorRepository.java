package com.example.suivreapp.repository;


import com.example.suivreapp.model.Doctor;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email); 
    


    Doctor findByUserId(Long userId);
}
