package com.example.suivreapp.controller;

import com.example.suivreapp.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping("/ask")
    public String askChatbot(@RequestBody String query) {
        return chatbotService.getChatbotResponse(query);
    }
}