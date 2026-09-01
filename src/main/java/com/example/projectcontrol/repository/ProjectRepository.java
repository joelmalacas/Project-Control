package com.example.projectcontrol.repository;

import com.example.projectcontrol.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p.signature FROM Project p WHERE p.id = :id")
    Optional<String> findSignatureById(@Param("id") Long id);
    boolean existsBySignature(String signature);
}