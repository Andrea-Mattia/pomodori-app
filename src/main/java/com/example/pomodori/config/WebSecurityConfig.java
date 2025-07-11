package com.example.pomodori.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.pomodori.service.AdminDetailsService;
import com.example.pomodori.service.DipendenteDetailsService;

@Configuration
public class WebSecurityConfig {

    private final AdminDetailsService adminDetailsService;
    private final DipendenteDetailsService dipendenteDetailsService;

    public WebSecurityConfig(AdminDetailsService adminDetailsService, DipendenteDetailsService dipendenteDetailsService) {
        this.adminDetailsService = adminDetailsService;
        this.dipendenteDetailsService = dipendenteDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/trigger-report").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/trigger-report") // 🔥 DISABILITA CSRF SOLO QUI
            )
            .formLogin(form -> form
                .loginPage("/custom-login")
                .loginProcessingUrl("/custom-login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/custom-login?logout")
                .permitAll()
            )
            .userDetailsService(adminDetailsService);

        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain dipendenteSecurity(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/scan", "/scan/**", "/dipendente/**")
            .authorizeHttpRequests(auth -> auth
            		.requestMatchers("/dipendente/login").permitAll()
            		.anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/dipendente/login")
                .loginProcessingUrl("/dipendente/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/dipendente/logout")
                .logoutSuccessUrl("/dipendente/login?logout")
            )
            .userDetailsService(dipendenteDetailsService);

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}