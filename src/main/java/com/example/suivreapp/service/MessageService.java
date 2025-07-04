package com.example.suivreapp.service;


import com.example.suivreapp.model.Message;
import com.example.suivreapp.model.Patient;
import com.example.suivreapp.repository.MessageRepository;
import com.example.suivreapp.repository.PatientRepository;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private  PatientRepository patientRepository;

    

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessages(Long patientId, Long doctorId) {
        return messageRepository.findByPatientIdAndDoctorId(patientId, doctorId);
    }
    public List<Message> getMessagesForDoctor(Long doctorId) {
        return messageRepository.findByDoctorId( doctorId);

}    }
