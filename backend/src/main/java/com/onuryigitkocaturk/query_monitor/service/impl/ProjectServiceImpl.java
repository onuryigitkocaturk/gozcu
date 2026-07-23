package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.dto.ProjectRequest;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateProjectException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.service.ProjectService;
import org.springframework.stereotype.Service;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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
}
