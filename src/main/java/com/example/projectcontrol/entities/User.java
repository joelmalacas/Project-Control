package com.example.projectcontrol.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serial;
import java.io.Serializable;
import java.util.regex.Pattern;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    // É preferível reusar uma única instância estática ou injetar via Service
    private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    public User() {}

    @JsonCreator
    public User(
            @JsonProperty("name") String name,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password
    ) {
        this.name = validName(name);
        this.email = validEmail(email);
        this.passwordHash = validPassword(password);
    }

    // ==== GETTERS ====
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getPasswordHash() {
        return passwordHash;
    }

    // ==== SETTERS ====
    public void setName(String name) {
        this.name = validName(name);
    }
    public void setEmail(String email) {
        this.email = validEmail(email);
    }
    public void setPassword(String password) {
        this.passwordHash = validPassword(password);
    }

    // ==== MÉTODOS DE VALIDAÇÃO ====
    private String validName(String n) {
        if (n == null || n.trim().length() < 3) {
            throw new IllegalArgumentException("Nome inválido, tem que conter no mínimo 3 caracteres.");
        }
        return n.trim();
    }

    private String validEmail(String e) {
        String regexEmail = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (e == null || !Pattern.matches(regexEmail, e)) {
            throw new IllegalArgumentException("E-mail inválido.");
        }
        return e.toLowerCase().trim();
    }

    private String validPassword(String p) {
        if (p == null || p.length() < 8) {
            throw new IllegalArgumentException("Password inválida, tem que conter no mínimo 8 caracteres.");
        }
        return encoder.encode(p);
    }
}