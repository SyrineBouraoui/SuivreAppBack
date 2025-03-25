package com.example.suivreapp.model;

public class AuthResponse {

private String token;

private String role;

private Long id; // User ID

private Long doctorId; // Doctor ID

private Long patientId; // Doctor ID



private UserDTO user;



// Constructor

public AuthResponse(Long id,Long patientId, Long doctorId, String token, Role role, UserDTO user) {

this.token = token;

this.role = role.name(); // Convert Role enum to String

this.id = id;

this.user = user;

this.doctorId = doctorId;

this.patientId = patientId;// Assign doctor ID

}



public Long getPatientId() {

return patientId;

}



public void setPatientId(Long patientId) {

this.patientId = patientId;

}



// Getters and setters

public Long getId() {

return id;

}



public void setId(Long id) {

this.id = id;

}



public Long getDoctorId() {

return doctorId;

}



public void setDoctorId(Long doctorId) {

this.doctorId = doctorId;

}



public String getToken() {

return token;

}



public void setToken(String token) {

this.token = token;

}



public String getRole() {

return role;

}



public void setRole(String role) {

this.role = role;

}



public UserDTO getUser() {

return user;

}



public void setUser(UserDTO user) {

this.user = user;

}

}











