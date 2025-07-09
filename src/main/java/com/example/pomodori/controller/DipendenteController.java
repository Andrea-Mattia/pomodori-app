package com.example.pomodori.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.pomodori.dto.DipendenteDto;
import com.example.pomodori.entity.Dipendente;
import com.example.pomodori.entity.TipoRuolo;
import com.example.pomodori.repository.DipendenteRepository;
import com.example.pomodori.repository.TipoRuoloRepository;
import com.example.pomodori.specification.DipendenteSpecification;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/dipendenti")
public class DipendenteController {

    @Autowired
    private DipendenteRepository dipendenteRepo;

    @Autowired
    private TipoRuoloRepository tipoRuoloRepo;

    @GetMapping
    public String list(@RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String codiceFiscale,
            @RequestParam(required = false) String soprannome,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
    	
    	Specification<Dipendente> spec = Specification
                .where(DipendenteSpecification.hasNome(nome))
                .and(DipendenteSpecification.hasCognome(cognome))
                .and(DipendenteSpecification.hasCodiceFiscale(codiceFiscale))
                .and(DipendenteSpecification.hasSoprannome(soprannome));
    	
    	Pageable pageable = PageRequest.of(page, size, Sort.by("cognome").descending());
        Page<Dipendente> resultPage = dipendenteRepo.findAll(spec, pageable);
    	
    	model.addAttribute("dipendenti", resultPage.getContent());
        model.addAttribute("page", resultPage);
        model.addAttribute("size", size);
        model.addAttribute("nome", nome);
        model.addAttribute("cognome", cognome);
        model.addAttribute("codiceFiscale", codiceFiscale);
        model.addAttribute("soprannome", soprannome);
        return "dipendente-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("dipendenteDto", new DipendenteDto());
        model.addAttribute("listaRuoli", tipoRuoloRepo.findAll());
        return "dipendente-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("dipendenteDto") DipendenteDto dto,
                       BindingResult result,
                       Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listaRuoli", tipoRuoloRepo.findAll());
            return "dipendente-form";
        }

        Dipendente dip = new Dipendente();
        dip.setNome(dto.getNome());
        dip.setCognome(dto.getCognome());
        dip.setCodiceFiscale(dto.getCodiceFiscale());
        if (dto.getSoprannome() != null && !dto.getSoprannome().isBlank()) {
        	dip.setSoprannome(dto.getSoprannome());
        }

        TipoRuolo ruolo = tipoRuoloRepo.findById(dto.getTipoRuoloId())
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non trovato"));
        dip.setTipoRuolo(ruolo);

        dipendenteRepo.save(dip);

        return "redirect:/admin/dipendenti?newDipAdded";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Dipendente dip = dipendenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dipendente non trovato"));

        DipendenteDto dto = new DipendenteDto();
        dto.setNome(dip.getNome());
        dto.setCognome(dip.getCognome());
        dto.setCodiceFiscale(dip.getCodiceFiscale());
        dto.setTipoRuoloId(dip.getTipoRuolo().getId());
        if (dip.getSoprannome() != null && !dip.getSoprannome().isBlank()) {
        	dto.setSoprannome(dip.getSoprannome());
        }

        model.addAttribute("dipendenteDto", dto);
        model.addAttribute("listaRuoli", tipoRuoloRepo.findAll());
        model.addAttribute("dipendenteId", id);

        return "dipendente-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("dipendenteDto") DipendenteDto dto,
                         BindingResult result,
                         Model model) {

        if (result.hasErrors()) {
            model.addAttribute("listaRuoli", tipoRuoloRepo.findAll());
            model.addAttribute("dipendenteId", id);
            return "dipendente-form";
        }

        Dipendente dip = dipendenteRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dipendente non trovato"));

        dip.setNome(dto.getNome());
        dip.setCognome(dto.getCognome());
        dip.setCodiceFiscale(dto.getCodiceFiscale());
        if (dto.getSoprannome() != null && !dto.getSoprannome().isBlank()) {
        	dip.setSoprannome(dto.getSoprannome());
        }

        TipoRuolo ruolo = tipoRuoloRepo.findById(dto.getTipoRuoloId())
                .orElseThrow(() -> new IllegalArgumentException("Ruolo non trovato"));
        dip.setTipoRuolo(ruolo);

        dipendenteRepo.save(dip);

        return "redirect:/admin/dipendenti?dipEditedSuccess";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        dipendenteRepo.deleteById(id);
        return "redirect:/admin/dipendenti?dipDeletedSuccess";
    }
}
