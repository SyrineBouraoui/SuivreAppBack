package com.example.suivreapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.suivreapp.service.SmsService;

import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private SmsService smsService;

    @PostMapping("/send-sms")
    public String sendAlert(@RequestBody Map<String, String> alertData) {
        try {
            String doctorPhone = alertData.get("doctorPhone");
            String patientName = alertData.get("patientName");

            // Check for abnormal values (you can extract these as doubles or integers)
            double heartRate = Double.parseDouble(alertData.get("heartRate"));
            double temperature = Double.parseDouble(alertData.get("temperature"));
            double oxygen = Double.parseDouble(alertData.get("oxygen"));
            double airTemperature = Double.parseDouble(alertData.get("airTemperature"));

            // Start the alert message with the patient's name
            StringBuilder alertMessage = new StringBuilder("Health Alert for " + patientName + ":\n");

            // Check for abnormal values and create the alert message
            if (heartRate < 60 || heartRate > 100) {
                alertMessage.append("High Heart Rate detected!\n");
            }
            if (temperature < 36 || temperature > 38) {
                alertMessage.append("Abnormal Temperature detected!\n");
            }
            if (oxygen < 90) {
                alertMessage.append("Low Oxygen Level detected!\n");
            }
            if (airTemperature < 15 || airTemperature > 30) {
                alertMessage.append("Extreme Air Temperature detected!\n");
            }

            // If the alert message contains any abnormal values, send the SMS
            if (alertMessage.length() > 0) {
                smsService.sendSms(doctorPhone, alertMessage.toString());
                return "✅ SMS alert sent successfully!";
            }

            // If no abnormal data is found, return this message
            return "No abnormal data detected.";
        } catch (Exception e) {
            return "❌ Failed to send SMS: " + e.getMessage();
        }
    }

}
