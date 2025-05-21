package com.example.suivreapp.repository;

import com.example.suivreapp.model.Heartrates;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HeartratesRepository extends JpaRepository<Heartrates, Long> {

	List<Heartrates> findByPatientId(String patientId);
}