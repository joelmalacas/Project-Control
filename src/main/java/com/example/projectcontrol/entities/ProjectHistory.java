package com.example.projectcontrol.entities;

import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "project_status_history")
public class ProjectHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull(message = Project.ERROR_ID)
    @Column(name = "user_id")
    private Long userId; // Nome alterado para respeitar a convenção camelCase em Java

    @NotNull(message = Project.ERROR_BLANK)
    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStateEnum projectStatus;

    @NotNull(message = Project.ERROR_ID)
    @Column(name = "project_id")
    private Long projectId;

    public ProjectHistory() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public ProjectStateEnum getProjectStatus() { return projectStatus; }
    public void setProjectStatus(ProjectStateEnum projectStatus) { this.projectStatus = projectStatus; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}