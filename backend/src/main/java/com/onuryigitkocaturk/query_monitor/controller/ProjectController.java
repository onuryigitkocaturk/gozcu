package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableResponse;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectMapper;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectTableMapper;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@PreAuthorize("hasRole('ADMIN')")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ProjectTableMapper projectTableMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper,
                              ProjectTableMapper projectTableMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.projectTableMapper = projectTableMapper;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(project));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> addUserToProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.addUserToProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromProject(@PathVariable Long projectId, @PathVariable Long userId) {
        projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/tables")
    public ResponseEntity<Void> addTableToProject(@PathVariable Long projectId,
                                                    @Valid @RequestBody ProjectTableRequest request) {
        projectService.addTableToProject(projectId, request.getTableName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/tables")
    public ResponseEntity<List<ProjectTableResponse>> getProjectTables(@PathVariable Long projectId) {
        List<ProjectTable> tables = projectService.getProjectTables(projectId);
        List<ProjectTableResponse> response = tables.stream()
                .map(projectTableMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/tables/{tableName}/data")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable Long projectId,
                                                                     @PathVariable String tableName) {
        return ResponseEntity.ok(projectService.getTableData(projectId, tableName));
    }
}
