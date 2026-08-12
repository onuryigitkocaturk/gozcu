package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.MyProjectResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectMemberResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectMembershipRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableRequest;
import com.onuryigitkocaturk.query_monitor.dto.ProjectTableResponse;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectMapper;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectMembershipMapper;
import com.onuryigitkocaturk.query_monitor.mapper.ProjectTableMapper;
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
import org.springframework.web.bind.annotation.PutMapping;
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

    private static final String AT_LEAST_REPORTER =
            "hasRole('ADMIN') or @projectAuthorizationService.isAtLeastReporter(principal.id, #projectId)";
    private static final String AT_LEAST_MAINTAINER =
            "hasRole('ADMIN') or @projectAuthorizationService.isAtLeastMaintainer(principal.id, #projectId)";

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final ProjectTableMapper projectTableMapper;
    private final ProjectMembershipMapper projectMembershipMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper,
                              ProjectTableMapper projectTableMapper, ProjectMembershipMapper projectMembershipMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.projectTableMapper = projectTableMapper;
        this.projectMembershipMapper = projectMembershipMapper;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request,
                                                           @AuthenticationPrincipal UserDetailsImpl principal) {
        Project project = projectService.createProject(request, principal.getId());
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
    public ResponseEntity<List<MyProjectResponse>> getMyProjects(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<MyProjectResponse> response = projectService.getProjectsForUser(principal.getId()).stream()
                .map(projectMembershipMapper::toMyProjectResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectMapper.toResponse(projectService.getProjectById(projectId)));
    }

    @PreAuthorize("hasRole('ADMIN') or @projectAuthorizationService.isOwner(principal.id, #id)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @PostMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> addUserToProject(@PathVariable UUID projectId, @PathVariable UUID userId,
                                                   @Valid @RequestBody ProjectMembershipRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        projectService.addUserToProject(projectId, userId, request.getRole(), principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @PutMapping("/{projectId}/users/{userId}/role")
    public ResponseEntity<Void> changeMemberRole(@PathVariable UUID projectId, @PathVariable UUID userId,
                                                   @Valid @RequestBody ProjectMembershipRequest request,
                                                   @AuthenticationPrincipal UserDetailsImpl principal) {
        projectService.changeMemberRole(projectId, userId, request.getRole(), principal.getId());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @DeleteMapping("/{projectId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromProject(@PathVariable UUID projectId, @PathVariable UUID userId) {
        projectService.removeUserFromProject(projectId, userId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{projectId}/users")
    public ResponseEntity<List<ProjectMemberResponse>> getProjectMembers(@PathVariable UUID projectId) {
        List<ProjectMemberResponse> response = projectService.getProjectMembers(projectId).stream()
                .map(projectMembershipMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @PostMapping("/{projectId}/tables")
    public ResponseEntity<Void> addTableToProject(@PathVariable UUID projectId,
                                                    @Valid @RequestBody ProjectTableRequest request) {
        projectService.addTableToProject(projectId, request.getTableName());
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize(AT_LEAST_MAINTAINER)
    @GetMapping("/{projectId}/discover-tables")
    public ResponseEntity<List<String>> discoverTables(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.discoverTables(projectId));
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{projectId}/tables")
    public ResponseEntity<List<ProjectTableResponse>> getProjectTables(@PathVariable UUID projectId) {
        List<ProjectTable> tables = projectService.getProjectTables(projectId);
        List<ProjectTableResponse> response = tables.stream()
                .map(projectTableMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{projectId}/tables/{tableName}/data")
    public ResponseEntity<List<Map<String, Object>>> getTableData(@PathVariable UUID projectId,
                                                                     @PathVariable String tableName) {
        return ResponseEntity.ok(projectService.getTableData(projectId, tableName));
    }

    @PreAuthorize(AT_LEAST_REPORTER)
    @GetMapping("/{projectId}/tables/{tableName}/columns")
    public ResponseEntity<List<String>> getTableColumns(@PathVariable UUID projectId,
                                                          @PathVariable String tableName) {
        return ResponseEntity.ok(projectService.getTableColumns(projectId, tableName));
    }
}
