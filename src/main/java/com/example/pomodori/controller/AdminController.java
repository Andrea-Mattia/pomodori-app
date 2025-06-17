package com.example.pomodori.controller;

import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.ScanRecordRepository;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

	@Autowired
	private ScanRecordRepository repository;

	@GetMapping("/admin")
	public String adminHome(Model model) {
		model.addAttribute("records", repository.findAll());
		return "admin-home";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/admin/export")
	public void exportCSV(HttpServletResponse response) throws IOException {
		response.setContentType("text/csv");
		response.setHeader("Content-Disposition", "attachment; filename=presenze.csv");

		List<ScanRecord> records = repository.findAll();
		PrintWriter writer = response.getWriter();
		writer.println("Nome,Cognome,Codice Fiscale,QR Code,Data,Ora");

		for (ScanRecord r : records) {
			String data = r.getScanTime().toLocalDate().toString();
			String ora = r.getScanTime().toLocalTime().toString();
			writer.printf("%s,%s,%s,%s,%s%n", r.getNome(), r.getCognome(),
					r.getCodiceFiscale() != null ? r.getCodiceFiscale() : "", r.getQrCode(), data, ora);
		}

		writer.flush();
		writer.close();
	}

}
