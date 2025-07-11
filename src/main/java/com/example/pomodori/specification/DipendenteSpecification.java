package com.example.pomodori.specification;

import org.springframework.data.jpa.domain.Specification;

import com.example.pomodori.entity.Dipendente;

public class DipendenteSpecification {

    public static Specification<Dipendente> hasNome(String nome) {
        return (root, query, cb) ->
                nome != null && !nome.isBlank() ? cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%") : null;
    }

    public static Specification<Dipendente> hasCognome(String cognome) {
        return (root, query, cb) ->
                cognome != null && !cognome.isBlank() ? cb.like(cb.lower(root.get("cognome")), "%" + cognome.toLowerCase() + "%") : null;
    }

    public static Specification<Dipendente> hasCodiceFiscale(String codiceFiscale) {
        return (root, query, cb) ->
        codiceFiscale != null && !codiceFiscale.isBlank() ? cb.like(cb.lower(root.get("codiceFiscale")), "%" + codiceFiscale.toLowerCase() + "%") : null;
    }
    
    public static Specification<Dipendente> hasSoprannome(String soprannome) {
    	return (root, query, cb) ->
    	soprannome != null && !soprannome.isBlank() ? cb.like(cb.lower(root.get("soprannome")), "%" + soprannome.toLowerCase() + "%") : null;
    }
    
    public static Specification<Dipendente> hasUsername(String username) {
    	return (root, query, cb) ->
    	username != null && !username.isBlank() ? cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%") : null;
    }
    
    public static Specification<Dipendente> hasRuolo(String ruoloDescrizione) {
        return (root, query, cb) -> {
            if (ruoloDescrizione != null && !ruoloDescrizione.isBlank()) {
                return cb.like(cb.lower(root.join("tipoRuolo").get("descrizione")), "%" + ruoloDescrizione.toLowerCase() + "%");
            }
            return null;
        };
    }

}

