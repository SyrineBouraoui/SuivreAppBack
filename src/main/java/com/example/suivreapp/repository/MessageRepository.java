package com.example.suivreapp.repository;


import com.example.suivreapp.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByPatientIdAndDoctorId(Long patientId, Long doctorId);
    List<Message> findByDoctorId(Long doctorId);


}
