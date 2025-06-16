package com.example.pomodori.controller;

import com.example.pomodori.repository.ScanRecordRepository;
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
}
