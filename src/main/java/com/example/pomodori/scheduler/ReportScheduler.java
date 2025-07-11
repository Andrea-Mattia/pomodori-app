package com.example.pomodori.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.pomodori.entity.ReportSetting;
import com.example.pomodori.entity.ScanRecord;
import com.example.pomodori.repository.ReportSettingRepository;
import com.example.pomodori.repository.ScanRecordRepository;

import jakarta.activation.DataSource;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Component
public class ReportScheduler {

    @Value("${report.schedule.cron}")
    private String cronExpression;

    private final ScanRecordRepository repository;
    private final JavaMailSender mailSender;
    private final ReportSettingRepository settingRepository;

    public ReportScheduler(ScanRecordRepository repository, JavaMailSender mailSender, ReportSettingRepository settingRepository) {
        this.repository = repository;
        this.mailSender = mailSender;
        this.settingRepository = settingRepository;
    }

    public void sendReportIfConfigured() {
    	ReportSetting setting = settingRepository.findById(1L).orElse(null);
        if (setting == null) return;
    	
    	LocalDate today = LocalDate.now();
        LocalDate lastSent = setting.getLastSentDate();
    	
    	if (shouldSendToday(setting.getFrequency(), today, lastSent)) {
    	    int days = "weekly".equalsIgnoreCase(setting.getFrequency()) ? 7 : 1;
    	    sendReportForDays(days);
    	    
    	    setting.setLastSentDate(today);
    	    settingRepository.save(setting);
    	}
    }

    private void sendReportForDays(int daysBack) {
        LocalDate fromDate = LocalDate.now().minusDays(daysBack);
        List<ScanRecord> recentRecords = repository.findAll().stream()
            .filter(r -> r.getScanTime().toLocalDate().isAfter(fromDate.minusDays(1)))
            .collect(Collectors.toList());

        if (!recentRecords.isEmpty()) {
            StringBuilder csv = new StringBuilder("Username,Nome,Cognome,Soprannome,Ruolo,Codice Fiscale,QR Code,Data,Ora\n");
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    		DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            for (ScanRecord r : recentRecords) {
            	String data = r.getScanTime().toLocalDate().format(dateFormatter);
    		    String ora = r.getScanTime().toLocalTime().format(timeFormatter);
                csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                	r.getUsername(),
                    r.getNome(), 
                    r.getCognome(),
                    r.getSoprannome() != null ? r.getSoprannome() : "",
                    r.getRuoloDescrizione(),
                    r.getCodiceFiscale() != null ? r.getCodiceFiscale() : "",
                    r.getQrCode(),
                    data,
                    ora));
            }

            sendEmail(csv.toString(), daysBack);
        }
    }

    private void sendEmail(String content, int daysBack) {
        ReportSetting setting = settingRepository.findById(1L).orElse(null);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8"); // multipart = true

            helper.setTo(setting.getAdminEmail());
            helper.setSubject("Report " + (daysBack == 1 ? "giornaliero" : "settimanale") + " - Presenze");
            helper.setText("In allegato il report delle ultime presenze in formato CSV.");

            // Prepara l'allegato come CSV
            DataSource dataSource = new ByteArrayDataSource(content, "text/csv");
            helper.addAttachment("report.csv", dataSource);

            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace(); // oppure log.error("Email failed", e);
        }
    }
    
    private boolean shouldSendToday(String frequency, LocalDate today, LocalDate lastSent) {
        if ("daily".equalsIgnoreCase(frequency)) {
            return lastSent == null || !lastSent.isEqual(today);
        }

        if ("weekly".equalsIgnoreCase(frequency)) {
            return (today.getDayOfWeek() == DayOfWeek.MONDAY) &&
                   (lastSent == null || !lastSent.isEqual(today));
        }

        return false;
    }
}
