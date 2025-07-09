package com.example.pomodori.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Table(name = "dipendenti")
public class Dipendente {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	private String nome;

	@NotBlank
	private String cognome;

	@NotBlank
	private String codiceFiscale;
	
	private String soprannome;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_ruolo_id")
    private TipoRuolo tipoRuolo;

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

	public String getSoprannome() {
		return soprannome;
	}

	public void setSoprannome(String soprannome) {
		this.soprannome = soprannome;
	}

	public TipoRuolo getTipoRuolo() {
		return tipoRuolo;
	}

	public void setTipoRuolo(TipoRuolo tipoRuolo) {
		this.tipoRuolo = tipoRuolo;
	}
}
