package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;

import java.time.LocalDateTime;
import java.util.UUID;

public class ProjectMemberResponse {

    private UUID userId;
    private String username;
    private String email;
    private ProjectRole role;
    private LocalDateTime joinedAt;

    public ProjectMemberResponse() {
    }

    public ProjectMemberResponse(UUID userId, String username, String email, ProjectRole role, LocalDateTime joinedAt) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
