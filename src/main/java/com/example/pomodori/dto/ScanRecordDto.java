package com.example.pomodori.dto;

import jakarta.validation.constraints.NotBlank;

public class ScanRecordDto {

	@NotBlank(message = "Lo username è obbligatorio")
	private String username;
	
	@NotBlank(message = "Il nome è obbligatorio")
	private String nome;

	@NotBlank(message = "Il cognome è obbligatorio")
	private String cognome;

	@NotBlank(message = "il codice fiscale è obbligatorio")
	private String codiceFiscale;

	@NotBlank(message = "QR code è obbligatorio")
	private String qrCode;
	
	private String soprannome;
	
	@NotBlank(message = "Il ruolo è obbligatorio")
	private String ruoloDescrizione;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getQrCode() {
		return qrCode;
	}

	public void setQrCode(String qrCode) {
		this.qrCode = qrCode;
	}

	public String getSoprannome() {
		return soprannome;
	}

	public void setSoprannome(String soprannome) {
		this.soprannome = soprannome;
	}

	public String getRuoloDescrizione() {
		return ruoloDescrizione;
	}

	public void setRuoloDescrizione(String ruoloDescrizione) {
		this.ruoloDescrizione = ruoloDescrizione;
	}
}
