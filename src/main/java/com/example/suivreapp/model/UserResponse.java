package com.example.suivreapp.model;

import java.util.List;

import com.example.suivreapp.controller.PatientController.PatientResponse;

public class UserResponse {
    private List<UserDTO> users;
    private UserDTO user;
    private List<PatientResponse> patients;

    // Constructor that accepts a List<UserDTO>
    public UserResponse(List<UserDTO> users) {
        this.users = users;
    }
    public UserResponse(UserDTO user) {
        this.user = user;
    }

    public UserDTO getUser() {
		return user;
	}
	public void setUser(UserDTO user) {
		this.user = user;
	}
	// Getter and Setter for users
    public List<UserDTO> getUsers() {
        return users;
    }

    public void setUsers(List<UserDTO> users) {
        this.users = users;
    }
  

       

        public UserResponse(UserDTO user, List<PatientResponse> patients) {
            this.user = user;
            this.patients = patients;
        }

       

        public List<PatientResponse> getPatients() {
            return patients;
        }

        public void setPatients(List<PatientResponse> patients) {
            this.patients = patients;
        }
    }
