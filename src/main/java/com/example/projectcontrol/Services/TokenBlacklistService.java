package com.example.projectcontrol.Services;

import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class TokenBlacklistService {

    // Guarda os tokens invalidados na memória RAM do servidor
    private final Set<String> blacklist = new HashSet<>();

    // Adiciona o token à lista negra (usado no logout)
    public void blacklistToken(String token) {
        blacklist.add(token);
    }

    // Verifica se o token já foi invalidado (usado no JwtAuthenticationFilter)
    public boolean isBlacklisted(String token) {
        return blacklist.contains(token);
    }
}