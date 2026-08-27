package com.example.projectcontrol.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Desativa a proteção CSRF obrigatória para chamadas REST/Postman
                .csrf(csrf -> csrf.disable())

                // 2. Permite o acesso a todas as rotas de desenvolvimento sem restrições
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/**").permitAll()
                );

        return http.build();
    }
}