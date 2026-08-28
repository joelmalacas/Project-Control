package com.example.projectcontrol.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "users")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 2L;

    public static final String NAME_ERROR = "Nome inválido, tem que conter no mínimo 3 caracteres.";
    public static final String EMAIL_ERROR = "E-mail inválido.";
    public static final String PASSWORD_ERROR = "Password inválida, tem que conter no mínimo 8 caracteres.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = NAME_ERROR)
    @Size(min = 3, message = NAME_ERROR)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = EMAIL_ERROR)
    @Email(message = EMAIL_ERROR)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = PASSWORD_ERROR)
    @Size(min = 8, message = PASSWORD_ERROR)
    private String password;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    // Construtor padrão obrigatório para o JPA e Jackson
    public User() {}

    // Getters e Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPasswordHash() { return passwordHash; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}