package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.ProjectTableResponse;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import org.springframework.stereotype.Component;

@Component
public class ProjectTableMapper {

    public ProjectTableResponse toResponse(ProjectTable projectTable) {
        return new ProjectTableResponse(
                projectTable.getId(),
                projectTable.getTableName(),
                projectTable.getCreatedAt()
        );
    }
}
