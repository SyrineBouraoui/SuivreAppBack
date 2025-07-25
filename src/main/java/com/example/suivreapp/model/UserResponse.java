package com.example.suivreapp.model;

import com.example.suivreapp.controller.PatientController.PatientResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;

public class UserResponse {
    private List<UserDTO> users;
    private UserDTO user;
    @JsonIgnore // Prevent serialization of patients
    private List<PatientResponse> patients;

    public UserResponse(List<UserDTO> users) {
        this.users = users;
    }

    public UserResponse(UserDTO user) {
        this.user = user;
    }

    public UserResponse(UserDTO user, List<PatientResponse> patients) {
        this.user = user;
        this.patients = patients;
    }

    public List<UserDTO> getUsers() { return users; }
    public void setUsers(List<UserDTO> users) { this.users = users; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
    public List<PatientResponse> getPatients() { return patients; }
    public void setPatients(List<PatientResponse> patients) { this.patients = patients; }
}