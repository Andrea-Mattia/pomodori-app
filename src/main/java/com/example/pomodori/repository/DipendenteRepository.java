package com.example.pomodori.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.pomodori.entity.Dipendente;

public interface DipendenteRepository extends JpaRepository<Dipendente, Long>, JpaSpecificationExecutor<Dipendente> {
	Optional<Dipendente> findByUsername(String username);
}
