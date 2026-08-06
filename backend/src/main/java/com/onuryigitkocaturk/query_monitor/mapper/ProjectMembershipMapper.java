package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.ProjectMemberResponse;
import com.onuryigitkocaturk.query_monitor.model.ProjectMembership;
import org.springframework.stereotype.Component;

@Component
public class ProjectMembershipMapper {

    public ProjectMemberResponse toResponse(ProjectMembership membership) {
        return new ProjectMemberResponse(
                membership.getUser().getId(),
                membership.getUser().getUsername(),
                membership.getUser().getEmail(),
                membership.getRole(),
                membership.getJoinedAt()
        );
    }
}
