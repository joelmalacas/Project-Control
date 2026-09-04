package com.example.projectcontrol.entities;

import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

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

    @NotBlank(message = Project.ERROR_ID)
    @Column(name = "user_id")
    private Long user_id;

    @NotBlank(message = Project.ERROR_BLANK)
    @Enumerated(EnumType.STRING)
    @Column(name = "project_status")
    private ProjectStateEnum projectStatus;

    @NotBlank(message = Project.ERROR_ID)
    @Column(name = "project_id")
    private Long projectId;

    public ProjectHistory() {}

    //====GETTER'S====
    public Long getId() { return id; }
    public Long getUserId() { return user_id; }
    public ProjectStateEnum getProjectStatus() { return projectStatus; }
    public Long getProjectId() { return projectId; }

    //====SETTER'S====
    public void setId(Long id) { this.id = id; }
    public void setUserId(Long user_id) { this.user_id = user_id; }
    public void setProjectStatus(ProjectStateEnum projectStatus) { this.projectStatus = projectStatus; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }
}