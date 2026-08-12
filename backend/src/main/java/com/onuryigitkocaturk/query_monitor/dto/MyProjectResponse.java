package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * GET /api/projects/my icin - ProjectResponse'tan farkli olarak, isteği
 * yapan kullanicinin O PROJEDEKI rolunu de tasir (ProjectMembership'ten
 * gelir). Baglanti bilgileri (dbHost/dbPort/dbName/dbUsername) bilerek
 * disarida birakildi - bu liste sadece "hangi projelerdeyim ve rolum ne"
 * sorusuna cevap verir, baglanti detayi icin GET /{projectId} kullanilir.
 */
public class MyProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private ProjectRole role;
    private LocalDateTime createdAt;

    public MyProjectResponse() {
    }

    public MyProjectResponse(UUID id, String name, String description, ProjectRole role, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.role = role;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
