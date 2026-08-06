package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import jakarta.validation.constraints.NotNull;

public class ProjectMembershipRequest {

    @NotNull
    private ProjectRole role;

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }
}
