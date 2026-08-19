package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import com.onuryigitkocaturk.query_monitor.repository.ProjectMembershipRepository;
import org.springframework.stereotype.Component;

import java.util.EnumSet;
import java.util.UUID;

// @PreAuthorize ifadeli için yetki karşılaştırması yapmak için olan sınıf.
// reporter < developer < maintainer < owner
@Component
public class ProjectAuthorizationService {

    private final ProjectMembershipRepository projectMembershipRepository;

    public ProjectAuthorizationService(ProjectMembershipRepository projectMembershipRepository) {
        this.projectMembershipRepository = projectMembershipRepository;
    }

    public boolean isAtLeastReporter(UUID userId, UUID projectId) {
        return hasAtLeastRole(userId, projectId, ProjectRole.REPORTER);
    }

    public boolean isAtLeastDeveloper(UUID userId, UUID projectId) {
        return hasAtLeastRole(userId, projectId, ProjectRole.DEVELOPER);
    }

    public boolean isAtLeastMaintainer(UUID userId, UUID projectId) {
        return hasAtLeastRole(userId, projectId, ProjectRole.MAINTAINER);
    }

    public boolean isAtLeastOwner(UUID userId, UUID projectId) {
        return hasAtLeastRole(userId, projectId, ProjectRole.OWNER);
    }

    private boolean hasAtLeastRole(UUID userId, UUID projectId, ProjectRole minRole) {
        EnumSet<ProjectRole> qualifyingRoles = EnumSet.noneOf(ProjectRole.class);
        for (ProjectRole role : ProjectRole.values()) {
            if (role.ordinal() >= minRole.ordinal()) {
                qualifyingRoles.add(role);
            }
        }
        return projectMembershipRepository.existsByProjectIdAndUserIdAndRoleIn(projectId, userId, qualifyingRoles);
    }
}
