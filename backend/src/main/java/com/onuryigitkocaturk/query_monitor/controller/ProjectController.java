package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableResponse;
import com.onuryigitkocaturk.query_monitor.dto.UserResponse;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectMapper;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectTableMapper;
import com.onuryigitkocaturk.query_monitor.mapper.UserMapper;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.security.UserDetailsImpl;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@PreAuthorize("hasRole('ADMIN')")
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ProjectTableMapper projectTableMapper;
    private final UserMapper userMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper,
                              ProjectTableMapper projectTableMapper, UserMapper userMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.projectTableMapper = projectTableMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        Project project = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectMapper.toResponse(project));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects() {
        List<ProjectResponse> response = projectService.getAllProjects().stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my")
    public ResponseEntity<List<ProjectResponse>> getMyProjects(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<ProjectResponse> response = projectService.getProjectsForUser(principal.getId()).stream()
                .map(projectMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> addUserToProject(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.addUserToProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromProject(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<UserResponse>> getProjectUsers(@PathVariable UUID projectId) {
        List<UserResponse> response = projectService.getProjectUsers(projectId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{projectId}/tables")
    public ResponseEntity<Void> addTableToProject(@PathVariable UUID projectId,
                                                    @Valid @RequestBody ProjectTableRequest request) {
        projectService.addTableToProject(projectId, request.getTableName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{projectId}/discover-tables")
    public ResponseEntity<List<String>> discoverTables(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.discoverTables(projectId));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/tables")
    public ResponseEntity<List<ProjectTableResponse>> getProjectTables(@PathVariable UUID projectId) {
        List<ProjectTable> tables = projectService.getProjectTables(projectId);
        List<ProjectTableResponse> response = tables.stream()
                .map(projectTableMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/tables/{tableName}/data")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable UUID projectId,
                                                                     @PathVariable String tableName) {
        return ResponseEntity.ok(projectService.getTableData(projectId, tableName));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{projectId}/tables/{tableName}/columns")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable UUID projectId,
                                                          @PathVariable String tableName) {
        return ResponseEntity.ok(projectService.getTableColumns(projectId, tableName));
    }
}
