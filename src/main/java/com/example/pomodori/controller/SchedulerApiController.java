package com.example.pomodori.controller;

import com.example.pomodori.scheduler.ReportScheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SchedulerApiController {

	@Value("${REPORT_TRIGGER_TOKEN}")
    private String expectedToken;
	
    private final ReportScheduler reportScheduler;

    public SchedulerApiController(ReportScheduler reportScheduler) {
        this.reportScheduler = reportScheduler;
    }

    @PostMapping("/trigger-report")
    public ResponseEntity<String> triggerReport(@RequestHeader("Authorization") String authHeader) {
        if (!("Bearer " + expectedToken).equals(authHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }
        reportScheduler.sendReportIfConfigured();
        return ResponseEntity.ok("Report inviato!");
    }
}
