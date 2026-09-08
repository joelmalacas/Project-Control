package com.example.projectcontrol.Security;

import jakarta.servlet.Filter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Desativar CSRF (obrigatorio para APIs REST/JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Não criar sessões HTTP (Stateless)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Definir permissões de acesso
                .authorizeHttpRequests(auth -> auth
                        // Permitir explicitamente qualquer método em /api/users e /api/users/login
                        .requestMatchers(HttpMethod.POST, "/api/users", "/api/users/login").permitAll()
                        .requestMatchers("/api/users/login").permitAll()
                        .anyRequest().authenticated()
                )

                // 4. Inserir o filtro JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Origens permitidas (ex: Angular, React, Vue ou Postman)
        // Em desenvolvimento podes usar "http://localhost:4200", "http://localhost:3000", etc.
        configuration.setAllowedOrigins(List.of("http://localhost:3000",
                "http://localhost:4200",
                "http://localhost:8080",
                "https://malacas.pt:8080",
                "http://192.168.1.81:8080",
                "https://salley-pursiest-apparently.ngrok-free.app:8080",
                "https://salley-pursiest-apparently.ngrok-free.app:4040",
                "https://salley-pursiest-apparently.ngrok-free.app"));

        // Se quiseres permitir QUALQUER origem em desenvolvimento (não recomendado em produção com credenciais):
        // configuration.addAllowedOriginPattern("*");

        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Cabeçalhos permitidos nos pedidos (incluindo o Authorization para o JWT)
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept"));

        // Cabeçalhos expostos na resposta ao cliente
        configuration.setExposedHeaders(List.of("Authorization"));

        // Permitir envio de cookies / credenciais se necessário
        configuration.setAllowCredentials(true);

        // Tempo de cache da resposta do preflight (em segundos)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todos os endpoints (/api/** ou /**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}