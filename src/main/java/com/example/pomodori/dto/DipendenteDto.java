package com.example.pomodori.dto;

import jakarta.validation.constraints.NotBlank;

public class DipendenteDto {

    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognome;

    @NotBlank(message = "Il codice fiscale è obbligatorio")
    private String codiceFiscale;
    
    private String soprannome;
    
    private Long tipoRuoloId;

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

	public String getSoprannome() {
		return soprannome;
	}

	public void setSoprannome(String soprannome) {
		this.soprannome = soprannome;
	}

	public Long getTipoRuoloId() {
		return tipoRuoloId;
	}

	public void setTipoRuoloId(Long tipoRuoloId) {
		this.tipoRuoloId = tipoRuoloId;
	}

}
