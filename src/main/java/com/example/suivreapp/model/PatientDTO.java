package com.example.suivreapp.model;

public class PatientDTO {
    private Long id;
    private String name;
    private String email;
    private Long user_id;
    private Long doctor_id;

    public PatientDTO(Long id, String name, String email, Long userId, Long doctorId) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.user_id = userId;
        this.doctor_id = doctorId;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public Long getUserId() { return user_id; }
    public Long getDoctorId() { return doctor_id; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setUserId(Long userId) { this.user_id = userId; }
    public void setDoctorId(Long doctorId) { this.doctor_id = doctorId; }
}