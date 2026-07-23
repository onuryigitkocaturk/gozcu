package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.ProjectResponse;
import com.onuryigitkocaturk.query_monitor.model.Project;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    public ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getCreatedAt()
        );
    }
}
