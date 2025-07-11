package com.example.pomodori.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.pomodori.details.DipendenteDetails;
import com.example.pomodori.repository.DipendenteRepository;

@Service
public class DipendenteDetailsService implements UserDetailsService {

    private final DipendenteRepository dipendenteRepository;

    public DipendenteDetailsService(DipendenteRepository dipendenteRepository) {
        this.dipendenteRepository = dipendenteRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return dipendenteRepository.findByUsername(username)
                .map(DipendenteDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Dipendente non trovato"));
    }
}
