package com.example.pomodori.details;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.pomodori.entity.Dipendente;

public class DipendenteDetails implements UserDetails {
	
	private static final long serialVersionUID = 5944128256665225241L;

	private final Dipendente dipendente;

	public DipendenteDetails(Dipendente dipendente) {
		this.dipendente = dipendente;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_DIPENDENTE"));
	}

	@Override
	public String getPassword() {
		return dipendente.getPasswordHash();
	}

	@Override
	public String getUsername() {
		return dipendente.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public Dipendente getDipendente() {
		return dipendente;
	}
}
