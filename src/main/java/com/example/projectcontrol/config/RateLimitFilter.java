package com.example.projectcontrol.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_TOKENS = 20;

    // Guarda 1 bucket de limite por cada IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Regra: Máximo tokens por minuto por IP
    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(MAX_TOKENS, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String clientIp = getClientIP(request);
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

        // Tenta consumir 1 token do bucket do IP
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            // Limite ultrapassado -> HTTP 429 Too Many Requests
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("text/plain;charset=UTF-8");
            response.getWriter().write("Muitos pedidos enviados. Tente novamente mais tarde.");
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}