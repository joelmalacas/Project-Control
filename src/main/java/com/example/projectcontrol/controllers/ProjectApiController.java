package com.example.projectcontrol.controllers;

import com.example.projectcontrol.Services.SignatureGenerateService;
import com.example.projectcontrol.entities.Enum.ProjectStateEnum;
import com.example.projectcontrol.entities.Project;
import com.example.projectcontrol.entities.ProjectHistory;
import com.example.projectcontrol.repository.ProjectHistoryRepository;
import com.example.projectcontrol.repository.ProjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/projects")
public class ProjectApiController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private SignatureGenerateService signatureGenerateService;

    @Autowired
    private ProjectHistoryRepository projectHistoryRepository;

    private Optional<Project> findProject;

    //Constants
    private static final int LENGTHSIGNATURE = 40;

    public ProjectApiController(ProjectRepository projectRepository,
                                SignatureGenerateService signatureGenerateService,
                                ProjectHistoryRepository projectHistoryRepository
    ) {
        this.projectRepository = projectRepository;
        this.signatureGenerateService = signatureGenerateService;
        this.projectHistoryRepository = projectHistoryRepository;
    }

    @GetMapping
    @ResponseBody
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    @GetMapping("/{id}/signature")
    @ResponseBody
    public ResponseEntity<?> findSignatureById(@PathVariable Long id) {
        String sign = String.valueOf(projectRepository.findSignatureById(id));

        Project projectSign = projectRepository.findById(id).orElse(null);

        if (sign.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Projeto não encontrado");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Objects.requireNonNull(projectSign).getSignature());
    }

    @GetMapping("/{sign}/id")
    @ResponseBody
    public ResponseEntity<?> findIdBySignature(@PathVariable String sign) {
        boolean exists = projectRepository.existsBySignature(sign);

        if (!exists)
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Projeto não encontrado");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(projectRepository.findBySignature(sign));
    }

    @GetMapping("/by-name/{name}/signature")
    @ResponseBody
    public ResponseEntity<?> findSignatureByName(@PathVariable String name) {
        if (name.trim().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Project.ERROR_BLANK);

        Optional<String> project = projectRepository.findSignatureByName(name);

        return project.<ResponseEntity<?>>map(s -> ResponseEntity
                .status(HttpStatus.OK)
                .body(s)).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Projeto não encontrado"));

    }

    @GetMapping("/{id}/status")
    public ResponseEntity<?> findStatusById(@PathVariable Long id) {
        findProject = projectRepository.findById(id);

        return findProject.<ResponseEntity<?>>map(project -> ResponseEntity
                .status(HttpStatus.OK)
                .body(project.getStatus())).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Projeto não encontrado"));

    }

    @GetMapping("/by-name/{name}/status")
    public ResponseEntity<?> findStatusByName(@PathVariable String name) {
        if (name.trim().isEmpty())
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Project.ERROR_BLANK);

       Optional<String> project = projectRepository.findStatusByName(name);

        return project.<ResponseEntity<?>>map(s -> ResponseEntity
                .status(HttpStatus.OK)
                .body(s)).orElseGet(() -> ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body("Projeto não encontrado"));
    }

    @PostMapping
    public ResponseEntity<?> createProject(@Valid @RequestBody Project project) {
        if (project.getId() != null && projectRepository.existsById(project.getId()))
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Project já existe");

        if (project.getUserId() == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Project.ERROR_BLANK);
        }

        //Generate Signature
        String signature;
        do {
            signature = signatureGenerateService.generateSignature(LENGTHSIGNATURE);
        } while (projectRepository.existsBySignature(signature));

        project.setSignature(signature);
        project.setUrl_PROD(project.getUrl_PROD());
        project.setUrl_REPO(project.getUrl_REPO());

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

        if (updates.containsKey("url_repo") && updates.get("url_repo") != null) {
            if (updates.get("URL_REPO").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setUrl_REPO(updates.get("url_repo").toString());
        }

        if (updates.containsKey("url_prod") && updates.get("url_prod") != null) {
            if (updates.get("URL_PROD").toString().isEmpty())
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(Project.ERROR_BLANK);
            projectExists.setUrl_PROD(updates.get("url_prod").toString());
        }

        Project saveProject = projectRepository.save(projectExists);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(saveProject);
    }

    @PutMapping("/{id}/generateSign")
    public ResponseEntity<?> refreshSignature(@PathVariable Long id) {
        Optional<Project> signRes = projectRepository.findById(id);

        if (signRes.isEmpty())
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Projeto não foi encontrado");

        Project project = signRes.get();

        String newSign = signatureGenerateService.generateSignature(LENGTHSIGNATURE);

        project.setSignature(newSign);
        projectRepository.save(project);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(project);
    }

    @PutMapping("/{id}/updateStatus")
    public ResponseEntity<?> updateProjectStatus(
            @PathVariable Long id,
            @Valid @RequestBody Map<String, String> statusMap,
            @RequestHeader(value = "X-User-Id", required = false) Long userId // Caso passes o ID do utilizador no cabeçalho
    ) {
        Optional<Project> updateRes = projectRepository.findById(id);

        if (updateRes.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Projeto não encontrado");
        }

        Project project = updateRes.get();

        if (!statusMap.containsKey("status") || statusMap.get("status") == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Project.ERROR_BLANK);
        }

        String statusStr = statusMap.get("status").trim().toUpperCase();

        if (statusStr.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Project.ERROR_BLANK);
        }

        try {
            ProjectStateEnum novoEstado = ProjectStateEnum.valueOf(statusStr);

            // 1. Atualiza a entidade principal
            project.setStatus(novoEstado.name());
            projectRepository.save(project);

            // 2. Regista o evento na tabela de histórico (ProjectHistory)
            ProjectHistory history = new ProjectHistory();
            history.setProjectId(project.getId());
            history.setProjectStatus(novoEstado);

            // Atribui o ID do utilizador (podes ajustar para obter da sessão/JWT/header)
            history.setUserId(userId != null ? userId : 1L);

            projectHistoryRepository.save(history);

            return ResponseEntity.ok(project);

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Estado inválido fornecido.");
        }
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
