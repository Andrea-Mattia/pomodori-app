package com.example.pomodori.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class ReportSetting {

	@Id
	private Long id = 1L;

	private String frequency; // "daily" o "weekly"

	private LocalDate lastSentDate;
	
	private String adminEmail;

	// Getters e setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public LocalDate getLastSentDate() {
		return lastSentDate;
	}

	public void setLastSentDate(LocalDate lastSentDate) {
		this.lastSentDate = lastSentDate;
	}

	public String getAdminEmail() {
		return adminEmail;
	}

	public void setAdminEmail(String adminEmail) {
		this.adminEmail = adminEmail;
	}
}
