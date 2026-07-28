package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;
import java.util.Map;

public interface ProjectService {

    Project createProject(ProjectRequest request);

    void deleteProject(Long id);

    List<Project> getAllProjects();

    List<Project> getProjectsForUser(Long userId);

    void addUserToProject(Long projectId, Long userId);

    void removeUserFromProject(Long projectId, Long userId);

    List<User> getProjectUsers(Long projectId);

    void addTableToProject(Long projectId, String tableName);

    List<ProjectTable> getProjectTables(Long projectId);

    List<Map<String, Object>> getTableData(Long projectId, String tableName);
}
