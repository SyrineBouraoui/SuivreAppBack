package com.example.suivreapp.model;

public class JwtResponse {

    private String jwt;

    public JwtResponse(String jwt) {
        this.jwt = jwt;
    }

    // Getter and Setter
    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }
}
