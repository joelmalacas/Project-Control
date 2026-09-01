package com.example.projectcontrol.controllers;

import com.example.projectcontrol.Services.SignatureGenerateService;
import com.example.projectcontrol.entities.Project;
import com.example.projectcontrol.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

    @GetMapping("/{id}/signature")
    @ResponseBody
    public ResponseEntity<?> findSignature(@PathVariable Long id) {
        String sign = String.valueOf(projectRepository.findSignatureById(id));

        Project projectSign = projectRepository.findById(id).orElse(null);

        if (sign.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        return ResponseEntity.status(HttpStatus.OK).body(projectSign);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProject(@PathVariable Long id, @Valid @RequestBody Map<String, Object> updates) {
        findProject = projectRepository.findById(id);

        if (findProject.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Projeto não encontrado");

        Project projectExists = findProject.get();

        //Update Fields
        if (updates.containsKey("name") && updates.get("name") != null) {
            if (updates.get("name").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setName(updates.get("name").toString());
        }

        if (updates.containsKey("description") && updates.get("description") != null) {
            if (updates.get("description").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setDescription(updates.get("description").toString());
        }

        if (updates.containsKey("repository_url") && updates.get("repository_url") != null) {
            if (updates.get("repository_url").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setURL_REPO(updates.get("repository_url").toString());
        }

        if (updates.containsKey("production_url") && updates.get("production_url") != null) {
            if (updates.get("production_url").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setURL_PROD(updates.get("production_url").toString());
        }

        Project saveProject = projectRepository.save(projectExists);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saveProject);
    }

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
