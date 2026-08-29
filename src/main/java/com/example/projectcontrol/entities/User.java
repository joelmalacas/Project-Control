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

    // Constants
    private static final int MIN_PASS_LENGTH = 8;
    private static final int MIN_NAME_LENGTH = 3;

    public static final String NAME_ERROR = "Nome inválido, tem que conter no mínimo " + MIN_NAME_LENGTH + " caracteres.";
    public static final String EMAIL_ERROR = "E-mail inválido.";
    public static final String PASSWORD_ERROR = "Password inválida, tem que conter no mínimo " + MIN_PASS_LENGTH + " caracteres.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = NAME_ERROR)
    @Size(min = MIN_NAME_LENGTH, message = NAME_ERROR)
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = EMAIL_ERROR)
    @Email(message = EMAIL_ERROR)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = PASSWORD_ERROR)
    @Size(min = MIN_PASS_LENGTH, message = PASSWORD_ERROR)
    private String password;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    public User() {}

    // Getters e Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getPasswordHash() { return passwordHash; }

    //Method Return constant values on Controller's
    public static int getMinPassLength() { return MIN_PASS_LENGTH; }
    public static int getMinNameLength() { return MIN_NAME_LENGTH; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    //Validation E-Mail
    public static boolean isValidEmail(String e) { return e != null && e.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"); }
}