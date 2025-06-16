package com.example.pomodori.controller;

import com.example.pomodori.dto.ScanRecordDto;
import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.ScanRecordRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class ScanController {

    @Autowired
    private ScanRecordRepository repository;

    @GetMapping("/scan")
    public String showForm(@RequestParam(name = "qr", required = false) String qrCode, Model model) {
        ScanRecordDto dto = new ScanRecordDto();
        dto.setQrCode(qrCode);
        model.addAttribute("scanRecordDto", dto);
        return "scan-form";
    }

    @PostMapping("/scan")
    public String saveScan(@Valid @ModelAttribute("scanRecordDto") ScanRecordDto dto,
                           BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            return "scan-form";
        }

        ScanRecord entity = new ScanRecord();
        entity.setNome(dto.getNome());
        entity.setCognome(dto.getCognome());
        entity.setCodiceFiscale(dto.getCodiceFiscale());
        entity.setQrCode(dto.getQrCode());

        repository.save(entity);
        model.addAttribute("message", "Dati salvati correttamente!");
        return "scan-success";
    }
}
