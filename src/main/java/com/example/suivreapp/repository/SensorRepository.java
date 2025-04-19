package com.example.suivreapp.repository;

import com.example.suivreapp.model.SensorData;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<SensorData, Long> {

	List<SensorData> findByPatientId(String patientId);
}