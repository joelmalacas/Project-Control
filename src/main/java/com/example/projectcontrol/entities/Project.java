package com.example.projectcontrol.entities;

import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.Id;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //ERROR CONSTANTS MESSAGES
    private static final String ERROR_SIGNATURE = "A assinatura deve ter 40 caracteres";
    private static final String ERROR_URL = "URL inválida";
    private static final String ERROR_BLANK = "Campo obrigatório";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProjectStateEnum statusEnum;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = ERROR_BLANK)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @NotBlank(message = ERROR_BLANK)
    @Size(min = 40, max = 40, message = ERROR_SIGNATURE)
    @Column(name = "signature", nullable = false)
    private String signature;

    @URL(message = ERROR_URL)
    @Column(name = "repository_url")
    private String URL_REPO;

    @URL(message = ERROR_URL)
    @Column(name = "production_url")
    private String URL_PROD;

   public Project() {}

    //====GETTER'S====
    public String getStatus() { return statusEnum.getValue(); }
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public String getSignature() {
        return signature;
    }
    public String getURL_REPO() { return URL_REPO; }
    public String getURL_PROD() { return URL_PROD; }

    //====SETTER'S====
    public void setStatus(String status) { this.statusEnum = ProjectStateEnum.valueOf(status); }
    public void setId(Long id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    public void setURL_REPO(String URL_REPO) { this.URL_REPO = URL_REPO; }
    public void setURL_PROD(String URL_PROD) { this.URL_PROD = URL_PROD; }
}
