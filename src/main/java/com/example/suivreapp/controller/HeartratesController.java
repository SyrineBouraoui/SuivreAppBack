package com.example.suivreapp.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.suivreapp.model.Heartrates;
import com.example.suivreapp.repository.HeartratesRepository;

@RestController
@RequestMapping("/api/heartrate")
public class HeartratesController {

	 @Autowired
	    private HeartratesRepository repository;

	    @PostMapping("/data")
	    public Heartrates saveHeartrates(@RequestBody Heartrates heartrates) {
	        return repository.save(heartrates);
	    }

	    @GetMapping("/data")
	    public List<Heartrates>  getHeartRate() {
	        return repository.findAll();
	    }
	    @GetMapping("/data/{patientId}")
	    public List<Heartrates> getHeartratesByPatientId(@PathVariable String patientId) {
	        return repository.findByPatientId(patientId);
	    }}