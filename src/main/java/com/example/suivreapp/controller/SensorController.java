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

import com.example.suivreapp.model.SensorData;
import com.example.suivreapp.repository.SensorRepository;
@RestController
@RequestMapping("/api/sensor")
public class SensorController {

    @Autowired
    private SensorRepository repository;

    @PostMapping("/data")
    public SensorData saveSensorData(@RequestBody SensorData sensorData) {
        return repository.save(sensorData);
    }

    @GetMapping("/data")
    public List<SensorData> getSensorData() {
        return repository.findAll();
    }
    @GetMapping("/data/{patientId}")
    public List<SensorData> getSensorDataByPatientId(@PathVariable String patientId) {
        return repository.findByPatientId(patientId);
    }
}