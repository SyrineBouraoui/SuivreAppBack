package com.example.suivreapp.repository;


import com.example.suivreapp.model.ChatbotResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatbotResponseRepository extends JpaRepository<ChatbotResponse, Long> {
}