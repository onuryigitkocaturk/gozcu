package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.model.Project;

public interface ProjectService {

    Project createProject(ProjectRequest request);

    void deleteProject(Long id);

    void addUserToProject(Long projectId, Long userId);

    void removeUserFromProject(Long projectId, Long userId);
}
