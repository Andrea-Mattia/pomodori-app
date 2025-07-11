package com.example.pomodori.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "scan_records")
public class ScanRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String username;
	
	@NotBlank
	private String nome;

	@NotBlank
	private String cognome;

	@NotBlank
	private String codiceFiscale;

	@NotBlank
	private String qrCode;
	
	private String soprannome;
	
	@NotBlank
	private String ruoloDescrizione;

	private LocalDateTime scanTime;
	
	public ScanRecord() {
		this.scanTime = LocalDateTime.now(ZoneId.of("Europe/Rome"));
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public LocalDateTime getScanTime() {
		return scanTime;
	}

	public void setScanTime(LocalDateTime scanTime) {
		this.scanTime = scanTime;
	}
}
