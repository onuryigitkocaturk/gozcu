package com.onuryigitkocaturk.query_monitor.model;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * User<->Project many-to-many ilişkisinin yerini alan ara entity. Eskiden
 * bu ilişki sadece "üye mi değil mi" bilgisini tutuyordu (JPA'nın otomatik
 * yönettiği user_project ara tablosu, ayrı bir entity YOKTU); artık rol
 * bilgisi (Reporter/Developer/Maintainer/Owner) de taşınması gerektiği
 * için, bilinçli olarak ara tabloyu kendi entity'sine çevirdik.
 */
@Entity
@Table(name = "project_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "project_id"}))
public class ProjectMembership {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime joinedAt;

    public ProjectMembership() {
    }

    public ProjectMembership(User user, Project project, ProjectRole role) {
        this.user = user;
        this.project = project;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMembership)) return false;
        ProjectMembership that = (ProjectMembership) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ProjectMembership{" +
                "id=" + id +
                ", role=" + role +
                '}';
    }
}
