package com.onuryigitkocaturk.query_monitor.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    /**
     * Query sonucunun degerlendirilecegi kosul (JSON: metric + operator + value).
     * @Lob KULLANILMAZ (bkz. Query.definitionJson'daki not).
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conditionExpression;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_id", nullable = false)
    private Query query;

    /**
     * Query.project ile aynı değeri taşır (denormalize) — Alert'ten Project'e
     * tek adımda erişebilmek için, Query üzerinden JOIN yapmaya gerek kalmasın diye.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Koşul sağlandığında mail bildiriminin gönderileceği kullanıcı grupları.
     * Bir alert birden fazla gruba mail atabilir.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "alert_group",
            joinColumns = @JoinColumn(name = "alert_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private Set<Group> groups = new HashSet<>();

    @OneToMany(mappedBy = "alert", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AlertLog> alertLogs = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Alert() {
    }

    public Alert(String conditionExpression, Query query, Project project, Set<Group> groups) {
        this.conditionExpression = conditionExpression;
        this.query = query;
        this.project = project;
        this.groups = groups;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    public List<AlertLog> getAlertLogs() {
        return alertLogs;
    }

    public void setAlertLogs(List<AlertLog> alertLogs) {
        this.alertLogs = alertLogs;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alert)) return false;
        Alert alert = (Alert) o;
        return Objects.equals(id, alert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Alert{" +
                "id=" + id +
                ", active=" + active +
                '}';
    }
}
