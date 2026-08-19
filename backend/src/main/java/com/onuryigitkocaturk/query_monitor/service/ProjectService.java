package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.ProjectDashboardStatsResponse;
import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectMembership;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProjectService {

    Project createProject(ProjectRequest request, UUID creatorUserId);

    void deleteProject(UUID id);

    List<Project> getAllProjects();

    Project getProjectById(UUID id);

    List<ProjectMembership> getProjectsForUser(UUID userId);

    void addUserToProject(UUID projectId, UUID userId, ProjectRole role, UUID actingUserId);

    void removeUserFromProject(UUID projectId, UUID userId);

    void changeMemberRole(UUID projectId, UUID userId, ProjectRole newRole, UUID actingUserId);

    List<ProjectMembership> getProjectMembers(UUID projectId);

    void addTableToProject(UUID projectId, String tableName);

    List<ProjectTable> getProjectTables(UUID projectId);

    List<Map<String, Object>> getTableData(UUID projectId, String tableName);

    List<String> discoverTables(UUID projectId);

    List<String> getTableColumns(UUID projectId, String tableName);

    ProjectDashboardStatsResponse getDashboardStats(UUID projectId);

    void removeTableFromProject(UUID projectId, UUID tableId);
}
