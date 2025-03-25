package com.example.suivreapp.model;
import java.util.List;
import com.example.suivreapp.model.Patient; // Import Patient class

public class DoctorDashboard {

    private String doctorName;
    private List<Patient> patients;

    // Getters and Setters
    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public List<Patient> getPatients() {
        return patients;
    }

    public void setPatients(List<Patient> patients) {
        this.patients = patients;
    }
}
