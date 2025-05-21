package com.example.suivreapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Heartrates {
	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;
	 	private Double heartRate;
	    private Double spo2;
	    private String timestamp;
	    private String patientId;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public Double getHeartRate() {
			return heartRate;
		}
		public void setHeartRate(Double heartRate) {
			this.heartRate = heartRate;
		}
		public Double getSpo2() {
			return spo2;
		}
		public void setSpo2(Double spo2) {
			this.spo2 = spo2;
		}
		public String getTimestamp() {
			return timestamp;
		}
		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}
		public String getPatientId() {
			return patientId;
		}
		public void setPatientId(String patientId) {
			this.patientId = patientId;
		}

}


