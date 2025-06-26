package com.example.pomodori.dto;

import java.time.LocalDate;

public class ReportSettingDto {

	private String frequency; // "daily" o "weekly"

	private LocalDate lastSentDate;
	
	private String adminEmail;
	
	private Integer timeoutMinutes;

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

	public Integer getTimeoutMinutes() {
		return timeoutMinutes;
	}

	public void setTimeoutMinutes(Integer timeoutMinutes) {
		this.timeoutMinutes = timeoutMinutes;
	}
}
