package com.example.projectcontrol.entities;

import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
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
    private String repositoryUrl;

    @URL(message = ERROR_URL)
    @Column(name = "production_url")
    private String productionUrl;

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
    public String getURL_REPO() { return repositoryUrl; }
    public String getURL_PROD() { return productionUrl; }
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
    public void setURL_REPO(String URL_REPO) { this.repositoryUrl = URL_REPO; }
    public void setURL_PROD(String URL_PROD) { this.productionUrl = URL_PROD; }
    public void setStatus(String status) { this.statusEnum = ProjectStateEnum.valueOf(status); }
}
