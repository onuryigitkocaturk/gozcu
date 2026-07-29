package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProjectService {

    Project createProject(ProjectRequest request);

    void deleteProject(UUID id);

    List<Project> getAllProjects();

    List<Project> getProjectsForUser(UUID userId);

    void addUserToProject(UUID projectId, UUID userId);

    void removeUserFromProject(UUID projectId, UUID userId);

    List<User> getProjectUsers(UUID projectId);

    void addTableToProject(UUID projectId, String tableName);

    List<ProjectTable> getProjectTables(UUID projectId);

    List<Map<String, Object>> getTableData(UUID projectId, String tableName);
}
