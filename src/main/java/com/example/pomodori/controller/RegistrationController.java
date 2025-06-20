package com.example.pomodori.controller;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.pomodori.dto.AdminDto;
import com.example.pomodori.dto.ResetPasswordDto;
import com.example.pomodori.entity.Admin;
import com.example.pomodori.repository.AdminRepository;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {

	private final AdminRepository repo;
	private final PasswordEncoder encoder;

	public RegistrationController(AdminRepository repo, PasswordEncoder encoder) {
		this.repo = repo;
		this.encoder = encoder;
	}

	@GetMapping("/register")
	public String showForm(Model model) {
		model.addAttribute("adminDto", new AdminDto());
		return "register";
	}

	@PostMapping("/register")
	public String register(@Valid @ModelAttribute("adminDto") AdminDto dto, BindingResult result, Model model) {
		if (!dto.getPassword().equals(dto.getConfirmPassword())) {
			result.rejectValue("confirmPassword", "error.confirmPassword", "Le password non corrispondono");
		}

		if (repo.findByUsername(dto.getUsername()).isPresent()) {
			result.rejectValue("username", "error.username", "Username già esistente");
		}

		if (result.hasErrors())
			return "register";

		Admin admin = new Admin();
		admin.setUsername(dto.getUsername());
		admin.setPassword(encoder.encode(dto.getPassword()));
		admin.setEmail(dto.getEmail());
		repo.save(admin);

		return "redirect:/login?registered";
	}

	@GetMapping("/custom-login")
	public String redirectToLoginOrRegister() {
		return repo.count() == 0 ? "redirect:/register" : "login";
	}

	@GetMapping("/reset-password")
	public String showResetForm(Model model) {
		model.addAttribute("resetDto", new ResetPasswordDto());
		return "reset-password";
	}

	@PostMapping("/reset-password")
	public String handleReset(@Valid @ModelAttribute("resetDto") ResetPasswordDto dto, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {

		if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
			bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Le password non coincidono.");
		}

		if (bindingResult.hasErrors()) {
			return "reset-password";
		}

		Optional<Admin> opt = repo.findByUsername(dto.getUsername());
		if (opt.isEmpty()) {
			bindingResult.rejectValue("username", "error.username", "Utente non trovato.");
			return "reset-password";
		}

		Admin admin = opt.get();
		admin.setPassword(encoder.encode(dto.getNewPassword()));
		repo.save(admin);

		redirectAttributes.addFlashAttribute("resetSuccess", "Password aggiornata con successo. Ora puoi accedere.");
		return "redirect:/login";
	}
}
