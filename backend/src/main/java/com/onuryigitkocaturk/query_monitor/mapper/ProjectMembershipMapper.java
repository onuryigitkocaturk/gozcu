package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.MyProjectResponse;
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

    // "Projelerim" listesinde her projenin yaninda kullanicinin O PROJEDEKI
    // rolunu gostermek icin - membership.getProject() JOIN FETCH ile geldigi
    // icin (bkz. ProjectMembershipRepository.findByUserId) ekstra sorguya girmez.
    public MyProjectResponse toMyProjectResponse(ProjectMembership membership) {
        return new MyProjectResponse(
                membership.getProject().getId(),
                membership.getProject().getName(),
                membership.getProject().getDescription(),
                membership.getRole(),
                membership.getProject().getCreatedAt()
        );
    }
}
