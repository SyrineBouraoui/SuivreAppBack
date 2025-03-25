package com.example.suivreapp.model;

import java.util.List;

public class DoctorResponse {
    private DoctorDTO doctor;
    private UserDTO user;
    private List<PatientResponse> patients;

    // Constructor with DoctorDTO and UserDTO
    public DoctorResponse(DoctorDTO doctor, UserDTO user) {
        this.doctor = doctor;
        this.user = user;
    }

    // Constructor with DoctorDTO, UserDTO, and Patients
    public DoctorResponse(DoctorDTO doctor, UserDTO user, List<PatientResponse> patients) {
        this.doctor = doctor;
        this.user = user;
        this.patients = patients;
    }

    // Getters and Setters
    public DoctorDTO getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorDTO doctor) {
        this.doctor = doctor;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public List<PatientResponse> getPatients() {
        return patients;
    }

 

	public void setPatients(List<com.example.suivreapp.controller.PatientController.PatientResponse> patientResponses) {
		   this.patients = patients;		
	}
}
