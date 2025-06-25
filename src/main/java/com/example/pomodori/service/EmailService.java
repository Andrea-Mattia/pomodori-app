package com.example.pomodori.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPresenceNotification(String nome, String cognome, String qrCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("andrea.mattia94@gmail.com"); // Cambia con l'email vera dell'amministratore
        message.setSubject("Nuova presenza registrata");
        message.setText(String.format("È stata registrata una nuova presenza:\n\nNome: %s\nCognome: %s\nQR Code: %s", 
                nome, cognome, qrCode));

        mailSender.send(message);
    }
    
    public void sendResetConfirmation(String email, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password aggiornata");
        message.setText(String.format("Ciao %s,\n\nLa tua password è stata aggiornata con successo.\n\nSe non sei stato tu, contatta immediatamente l'amministratore.", username));
        mailSender.send(message);
    }

}
