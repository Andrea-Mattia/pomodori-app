package com.example.pomodori.specification;

import org.springframework.data.jpa.domain.Specification;
import com.example.pomodori.entity.ScanRecord;

import java.time.LocalDate;

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

    public static Specification<ScanRecord> hasData(LocalDate data) {
        return (root, query, cb) ->
                data != null ? cb.between(root.get("scanTime"),
                        data.atStartOfDay(), data.plusDays(1).atStartOfDay()) : null;
    }
}

