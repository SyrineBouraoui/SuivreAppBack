package com.example.suivreapp.controller;


import com.example.suivreapp.model.Message;
import com.example.suivreapp.model.Role;
import com.example.suivreapp.model.User;
import com.example.suivreapp.repository.UserRepository;
import com.example.suivreapp.service.JwtService;
import com.example.suivreapp.service.MessageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;



@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "http://localhost:4200") // Allow Angular frontend
public class MessaggeController {
    private final MessageService messageService;

     private UserRepository userRepository;

    @Autowired
    private JwtService JwtService;

    public MessaggeController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public Message sendMessage(@RequestBody Message message) {
        // Check if the sender is 'patient', and if patientName is not included, fetch it from the database
        if (message.getSender().equals("patient") && message.getPatientName() == null) {
            Optional<User> patient = userRepository.findById(message.getPatientId());
            if (patient.isPresent()) {
                message.setPatientName(patient.get().getUsername());  // Set patientName from the database
            }
        }

        // Send the message to the service layer
        return messageService.sendMessage(message);
    }


    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getMessagesForDoctor(@PathVariable Long doctorId) {
        // Step 1: Fetch the messages for the specified doctor
        List<Message> messages = messageService.getMessagesForDoctor(doctorId);

        // Step 2: Return the messages in the response
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/{patientId}/{doctorId}")
    public List<Message> getMessages(@PathVariable Long patientId, @PathVariable Long doctorId) {
        // Fetch messages between the patient and the doctor
        return messageService.getMessages(patientId, doctorId);
    }
}