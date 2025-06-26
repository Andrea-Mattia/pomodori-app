package com.example.pomodori.controller;

import com.example.pomodori.dto.ReportSettingDto;
import com.example.pomodori.entity.Admin;
import com.example.pomodori.entity.ReportSetting;
import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.AdminRepository;
import com.example.pomodori.repository.ReportSettingRepository;
import com.example.pomodori.repository.ScanRecordRepository;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
	public String adminHome(Model model, Principal principal) {
		model.addAttribute("records", repository.findAll());
		model.addAttribute("username", principal.getName());
		model.addAttribute("timeoutMinutes", repo.findById(1L).map(ReportSetting::getTimeoutMinutes).orElse(15));
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

	
	@GetMapping("/admin/export")
	public void exportCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=presenze.csv");

		List<ScanRecord> records = repository.findAll();
		PrintWriter writer = response.getWriter();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

		writer.println("Nome,Cognome,Codice Fiscale,QR Code,Data,Ora");

		for (ScanRecord r : records) {
		    String data = r.getScanTime().toLocalDate().format(dateFormatter);
		    String ora = r.getScanTime().toLocalTime().format(timeFormatter);
		    writer.printf("%s,%s,%s,%s,%s,%s%n",
		        r.getNome(),
		        r.getCognome(),
		        r.getCodiceFiscale() != null ? r.getCodiceFiscale() : "",
		        r.getQrCode(),
		        data,
		        ora
		    );
		}

		writer.flush();
		writer.close();
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
