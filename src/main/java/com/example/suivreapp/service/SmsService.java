package com.example.suivreapp.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account_sid}")
    private String accountSid;

    @Value("${twilio.auth_token}")
    private String authToken;

    @Value("${twilio.phone_number}")
    private String twilioPhoneNumber;

    public void sendSms(String doctorPhone, String alertMessage) {
        if (doctorPhone == null || doctorPhone.isEmpty()) {
            throw new IllegalArgumentException("Doctor's phone number is missing!");
        }
        if (alertMessage == null || alertMessage.isEmpty()) {
            throw new IllegalArgumentException("Alert message is empty!");
        }

        Twilio.init(accountSid, authToken);

        Message.creator(
                new com.twilio.type.PhoneNumber(doctorPhone),  // Doctor's phone
                new com.twilio.type.PhoneNumber(twilioPhoneNumber),  // Twilio phone
                alertMessage
        ).create();

        System.out.println("📲 SMS sent to doctor: " + doctorPhone);
    }
}
