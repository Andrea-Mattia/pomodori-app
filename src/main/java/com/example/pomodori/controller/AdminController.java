package com.example.pomodori.controller;

import com.example.pomodori.dto.ReportSettingDto;
import com.example.pomodori.entity.Admin;
import com.example.pomodori.entity.ReportSetting;
import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.AdminRepository;
import com.example.pomodori.repository.ReportSettingRepository;
import com.example.pomodori.repository.ScanRecordRepository;
import com.example.pomodori.specification.ScanRecordSpecification;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminController {

	@Autowired
	private ScanRecordRepository repository;
	
	@Autowired
	private AdminRepository adminRepository;
	
	private final ReportSettingRepository repo;
	
    public AdminController(ReportSettingRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/admin")
    public String adminHome(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String qrCode,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            Principal principal) {

        Specification<ScanRecord> spec = Specification
            .where(ScanRecordSpecification.hasNome(nome))
            .and(ScanRecordSpecification.hasCognome(cognome))
            .and(ScanRecordSpecification.hasQrCode(qrCode))
            .and(ScanRecordSpecification.hasData(data));

        Pageable pageable = PageRequest.of(page, size, Sort.by("scanTime").descending());
        Page<ScanRecord> resultPage = repository.findAll(spec, pageable);

        model.addAttribute("records", resultPage.getContent());
        model.addAttribute("page", resultPage);
        model.addAttribute("size", size);
        model.addAttribute("username", principal.getName());
        model.addAttribute("nome", nome);
        model.addAttribute("cognome", cognome);
        model.addAttribute("qrCode", qrCode);
        model.addAttribute("data", data);
        model.addAttribute("timeoutMinutes", repo.findById(1L).map(ReportSetting::getTimeoutMinutes).orElse(10));
        return "admin-home";
    }

	@GetMapping("/admin/login")
	public String login(Model model) {
		return "login";
	}
	
	@GetMapping("/admin/keep-alive")
	@ResponseBody
	public String keepAlive() {
	    return "OK";
	}

	@GetMapping("/admin/export-page")
	public String export(Model model) {
		return "export-page";
	}
	
	@GetMapping("/admin/export")
	public void exportCSV(
	        @RequestParam(required = false) String nome,
	        @RequestParam(required = false) String cognome,
	        @RequestParam(required = false) String qrCode,
	        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
	        HttpServletResponse response) throws IOException {

	    Specification<ScanRecord> spec = Specification
	        .where(ScanRecordSpecification.hasNome(nome))
	        .and(ScanRecordSpecification.hasCognome(cognome))
	        .and(ScanRecordSpecification.hasQrCode(qrCode))
	        .and(ScanRecordSpecification.hasData(data));

	    List<ScanRecord> records = repository.findAll(spec);

	    response.setContentType("text/csv");
	    response.setHeader("Content-Disposition", "attachment; filename=presenze.csv");

	    PrintWriter writer = response.getWriter();
	    writer.println("Nome,Cognome,Codice Fiscale,QR Code,Data,Ora");
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

	    for (ScanRecord r : records) {
	        writer.printf("%s,%s,%s,%s,%s,%s%n",
	            r.getNome(),
	            r.getCognome(),
	            r.getCodiceFiscale() != null ? r.getCodiceFiscale() : "",
	            r.getQrCode(),
	            r.getScanTime().toLocalDate().format(dateFormatter),
	            r.getScanTime().toLocalTime().format(timeFormatter));
	    }
	}
	
	@GetMapping("/admin/settings")
    public String showSettings(Model model) {
        ReportSetting setting = repo.findById(1L).orElseGet(() -> {
            ReportSetting defaultSetting = new ReportSetting();
            Admin admin = adminRepository.findAll().getFirst();
            defaultSetting.setFrequency("daily");
            defaultSetting.setAdminEmail(admin.getEmail());
            defaultSetting.setTimeoutMinutes(15);
            return repo.save(defaultSetting);
        });

        ReportSettingDto dto = new ReportSettingDto();
        dto.setFrequency(setting.getFrequency());
        dto.setAdminEmail(setting.getAdminEmail());
        dto.setTimeoutMinutes(setting.getTimeoutMinutes() != null ? setting.getTimeoutMinutes() : 15);
        model.addAttribute("reportSettings", dto);
        return "admin-settings";
    }

    @PostMapping("/admin/settings")
    public String updateSettings(@ModelAttribute ReportSettingDto dto, RedirectAttributes redirectAttributes) {
        ReportSetting setting = repo.findById(1L).orElse(new ReportSetting());
        setting.setFrequency(dto.getFrequency());
        setting.setAdminEmail(dto.getAdminEmail());
        setting.setTimeoutMinutes(dto.getTimeoutMinutes());
        setting.setId(1L);
        repo.save(setting);

        redirectAttributes.addFlashAttribute("message", "Frequenza aggiornata con successo.");
        return "redirect:/admin/settings";
    }

}
