package com.example.suivreapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "chatbot_responses")
public class ChatbotResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }
    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
}