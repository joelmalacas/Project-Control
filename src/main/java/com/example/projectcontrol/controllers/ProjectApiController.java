package com.example.projectcontrol.controllers;

import com.example.projectcontrol.Services.SignatureGenerateService;
import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import com.example.projectcontrol.entities.Project;
import com.example.projectcontrol.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SignatureGenerateService signatureGenerateService;

    Optional<Project> findProject;

    public ProjectApiController(ProjectRepository projectRepository, SignatureGenerateService signatureGenerateService) {
        this.projectRepository = projectRepository;
        this.signatureGenerateService = signatureGenerateService;
    }

    @GetMapping
    @ResponseBody
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        if (projectRepository.existsById(project.getId()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Project já existe");

        //Generate Signature
        String signature = signatureGenerateService.generateSignature(40);

        project.setSignature(signature);

       Project projectSave =  projectRepository.save(project);

       return ResponseEntity.status(HttpStatus.CREATED).body(projectSave);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        findProject = projectRepository.findById(id);

        if (findProject.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Projeto não encontrado");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(findProject.get());
    }

    //TODO PUT

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProject(@PathVariable Long id) {
       findProject = projectRepository.findById(id);

       if (findProject.isEmpty())
           return ResponseEntity
                   .status(HttpStatus.NOT_FOUND)
                   .body("Projeto não existe");

       projectRepository.delete(findProject.get());

       return ResponseEntity
               .status(HttpStatus.OK)
               .body("Projeto excluído com sucesso");
    }
}
