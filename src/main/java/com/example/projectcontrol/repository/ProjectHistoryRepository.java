package com.example.projectcontrol.repository;

import com.example.projectcontrol.entities.ProjectHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectHistoryRepository extends JpaRepository<ProjectHistory, Long> {
    List<ProjectHistory> findByProjectId(Long projectId);
}
