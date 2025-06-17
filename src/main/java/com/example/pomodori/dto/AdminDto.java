package com.example.pomodori.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AdminDto {
    @NotBlank(message = "Username è obbligatorio")
    private String username;

    @NotBlank(message = "La password è obbligatoria")
    private String password;

    @NotBlank(message = "Ripeti la password")
    private String confirmPassword;

    @NotBlank(message = "La email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
