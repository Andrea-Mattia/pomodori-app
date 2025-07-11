package com.example.pomodori.specification;

import java.time.LocalDate;

import org.springframework.data.jpa.domain.Specification;

import com.example.pomodori.entity.ScanRecord;

public class ScanRecordSpecification {

    public static Specification<ScanRecord> hasNome(String nome) {
        return (root, query, cb) ->
                nome != null && !nome.isBlank() ? cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%") : null;
    }

    public static Specification<ScanRecord> hasCognome(String cognome) {
        return (root, query, cb) ->
                cognome != null && !cognome.isBlank() ? cb.like(cb.lower(root.get("cognome")), "%" + cognome.toLowerCase() + "%") : null;
    }

    public static Specification<ScanRecord> hasQrCode(String qrCode) {
        return (root, query, cb) ->
                qrCode != null && !qrCode.isBlank() ? cb.like(cb.lower(root.get("qrCode")), "%" + qrCode.toLowerCase() + "%") : null;
    }
    
    public static Specification<ScanRecord> hasSoprannome(String soprannome) {
    	return (root, query, cb) ->
    	soprannome != null && !soprannome.isBlank() ? cb.like(cb.lower(root.get("soprannome")), "%" + soprannome.toLowerCase() + "%") : null;
    }
    
    public static Specification<ScanRecord> hasUsername(String username) {
    	return (root, query, cb) ->
    	username != null && !username.isBlank() ? cb.like(cb.lower(root.get("username")), "%" + username.toLowerCase() + "%") : null;
    }
    
    public static Specification<ScanRecord> hasRuoloDescrizione(String ruoloDescrizione) {
    	return (root, query, cb) ->
    	ruoloDescrizione != null && !ruoloDescrizione.isBlank() ? cb.like(cb.lower(root.get("ruoloDescrizione")), "%" + ruoloDescrizione.toLowerCase() + "%") : null;
    }

    public static Specification<ScanRecord> hasData(LocalDate data) {
        return (root, query, cb) ->
                data != null ? cb.between(root.get("scanTime"),
                        data.atStartOfDay(), data.plusDays(1).atStartOfDay()) : null;
    }
}

