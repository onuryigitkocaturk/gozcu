package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectException;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectTableException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.TableNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectTableRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectTableRepository projectTableRepository;
    private final TableMetadataService tableMetadataService;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                               UserRepository userRepository,
                               ProjectTableRepository projectTableRepository,
                               TableMetadataService tableMetadataService) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.projectTableRepository = projectTableRepository;
        this.tableMetadataService = tableMetadataService;
    }

    @Override
    public Project createProject(ProjectRequest request) {
        if (projectRepository.existsByName(request.getName())) {
            throw new DuplicateProjectException("Project already exists: " + request.getName());
        }

        Project project = new Project(request.getName(), request.getDescription());
        return projectRepository.save(project);
    }

    @Override
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ProjectNotFoundException("Project not found: " + id);
        }
        projectRepository.deleteById(id);
    }

    @Override
    public void addUserToProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.getProjects().add(project);
        userRepository.save(user);
    }

    @Override
    public void removeUserFromProject(Long projectId, Long userId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + projectId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.getProjects().remove(project);
        userRepository.save(user);
    }

    @Override
    public void addTableToProject(Long projectId, String tableName) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + projectId));

        if (!tableMetadataService.listTables().contains(tableName)) {
            throw new TableNotFoundException("Table not found in monitored database: " + tableName);
        }

        if (projectTableRepository.existsByProjectIdAndTableName(projectId, tableName)) {
            throw new DuplicateProjectTableException(
                    "Table already added to this project: " + tableName);
        }

        ProjectTable projectTable = new ProjectTable(tableName, project);
        projectTableRepository.save(projectTable);
    }

    @Override
    public List<ProjectTable> getProjectTables(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found: " + projectId);
        }
        return projectTableRepository.findByProjectId(projectId);
    }
}
