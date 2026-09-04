package com.example.projectcontrol.entities;

import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "projects")
public class Project implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    //ERROR CONSTANTS MESSAGES
    public static final String ERROR_ID = "ID inválido";
    public static final String ERROR_SIGNATURE = "A assinatura deve ter 40 caracteres";
    public static final String ERROR_URL = "URL inválida";
    public static final String ERROR_BLANK = "Campo obrigatório";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @NotBlank(message = ERROR_BLANK)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Size(min = 40, max = 40, message = ERROR_SIGNATURE)
    @Column(name = "signature", nullable = false)
    private String signature;

    @URL(message = ERROR_URL)
    @Column(name = "repository_url")
    private String url_REPO;

    @URL(message = ERROR_URL)
    @Column(name = "production_url")
    private String url_PROD;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProjectStateEnum statusEnum = ProjectStateEnum.ACTIVE;

    public Project() {}

    //====GETTER'S====
    public Long getId() {
        return id;
    }
    public Long getUserId() {
        return userId;
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
    @JsonProperty("url_REPO")
    public String getUrl_REPO() { return url_REPO; }
    @JsonProperty("url_PROD")
    public String getUrl_PROD() { return url_PROD; }
    public String getStatus() {
        return statusEnum != null ? statusEnum.getValue() : null;
    }

    //====SETTER'S====
    public void setId(Long id) {
        this.id = id;
    }
    public void setUserId(Long id) { this.userId = id; }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setSignature(String signature) {
        this.signature = signature;
    }
    @JsonProperty("url_REPO")
    public void setUrl_REPO(String url_REPO) { this.url_REPO = url_REPO; }
    @JsonProperty("url_PROD")
    public void setUrl_PROD(String url_PROD) { this.url_PROD = url_PROD; }
    public void setStatus(String status) {
        if (status != null) {
            this.statusEnum = ProjectStateEnum.valueOf(status);
        }
    }
}