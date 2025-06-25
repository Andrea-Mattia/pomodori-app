package com.example.pomodori;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PomodoriApplication {

    public static void main(String[] args) {
        SpringApplication.run(PomodoriApplication.class, args);
    }

}
