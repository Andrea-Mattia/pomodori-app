package com.example.pomodori.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.pomodori.dto.ScanRecordDto;
import com.example.pomodori.entity.Dipendente;
import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.DipendenteRepository;
import com.example.pomodori.repository.ScanRecordRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class ScanController {

    @Autowired
    private ScanRecordRepository repository;
    
    @Autowired
    private DipendenteRepository dipendenteRepository;
    
//    @Autowired
//    private EmailService emailService;
    
    @GetMapping("/")
    public String getHomePage() {
    	return "redirect:/home";
    }
    
    @GetMapping("/home")
    public String home(Principal principal, Model model) {
    	if (principal != null) {
    		Dipendente dip = dipendenteRepository.findByUsername(principal.getName()).orElse(null);
        	if (dip == null) {
        		return "redirect:/dipendente/login?error";
        	}
            model.addAttribute("dipendente", dip);
    	} else {
    		return "redirect:/dipendente/login";
    	}
    	
        return "home";
    }
    
    @GetMapping("/dipendente/login")
    public String loginDipendente() {
        return "dipendente-login";
    }
    
    @GetMapping("/scan")
    public String scanForm(@RequestParam(name = "qr", required = false) String qr, 
                           Principal principal, 
                           HttpSession session,
                           Model model) {
        if (qr != null) {
            session.setAttribute("qrCode", qr);
            return "redirect:/scan";
        }

        String sessionQr = (String) session.getAttribute("qrCode");
        if (sessionQr == null) {
            return "redirect:/home?error";
        }

        Dipendente dip = dipendenteRepository.findByUsername(principal.getName()).orElseThrow();

        ScanRecordDto dto = new ScanRecordDto();
        dto.setUsername(dip.getUsername());
        dto.setNome(dip.getNome());
        dto.setCognome(dip.getCognome());
        dto.setCodiceFiscale(dip.getCodiceFiscale());
        dto.setRuoloDescrizione(dip.getTipoRuolo().getDescrizione());
        dto.setQrCode(sessionQr);
        if (dip.getSoprannome() != null) {
            dto.setSoprannome(dip.getSoprannome());
        }

        model.addAttribute("scanRecordDto", dto);
        return "scan-form";
    }


    @PostMapping("/scan")
    public String saveScan(@Valid @ModelAttribute("scanRecordDto") ScanRecordDto dto,
                           BindingResult bindingResult,
                           Model model,
                           HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "scan-form";
        }

        ScanRecord entity = new ScanRecord();
        entity.setUsername(dto.getUsername());
        entity.setNome(dto.getNome());
        entity.setCognome(dto.getCognome());
        entity.setCodiceFiscale(dto.getCodiceFiscale());
        entity.setQrCode(dto.getQrCode());
        if (dto.getSoprannome() != null) {
        	entity.setSoprannome(dto.getSoprannome());
        }
        entity.setRuoloDescrizione(dto.getRuoloDescrizione());

        repository.save(entity);
        session.removeAttribute("qrCode");
//        emailService.sendPresenceNotification(entity.getNome(), entity.getCognome(), entity.getQrCode());
        
        return "redirect:/home?success";
    }
    
    @PostMapping(value = "/scan", consumes = "application/json")
    @ResponseBody
    public ResponseEntity<String> saveScanOffline(@Valid @RequestBody ScanRecordDto dto) {
        ScanRecord entity = new ScanRecord();
        entity.setUsername(dto.getUsername());
        entity.setNome(dto.getNome());
        entity.setCognome(dto.getCognome());
        entity.setCodiceFiscale(dto.getCodiceFiscale());
        entity.setQrCode(dto.getQrCode());
        if (dto.getSoprannome() != null) {
            entity.setSoprannome(dto.getSoprannome());
        }
        entity.setRuoloDescrizione(dto.getRuoloDescrizione());

        repository.save(entity);
        return ResponseEntity.ok("Salvato offline");
    }

}
