package com.onuryigitkocaturk.query_monitor.model;

import com.onuryigitkocaturk.query_monitor.enums.Frequency;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "queries")
public class Query {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    /**
     * Surukle-birak query builder'in urettigi JSON agaci (QueryNode). @Lob
     * KULLANILMAZ - PostgreSQL'de @Lob, gercek TEXT yerine ozel bir Large
     * Object (oid) mekanizmasina denk geliyor ve autocommit disi (transactional)
     * erisim zorunlu kiliyor; TEXT kolonunda boyut siniri olmadigi icin buna
     * hic gerek yok.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String definitionJson;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;

    @Column(nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * Bir proje birden fazla tabloyu izleyebildigi icin (Project -> ProjectTable
     * one-to-many), Query'nin hangi tabloyu sorguladigini belirtmek icin ayrica
     * gereklidir. projectTable.getProject() ile project alani her zaman ayni
     * projeye isaret etmelidir (servis katmaninda dogrulanir).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_table_id", nullable = false)
    private ProjectTable projectTable;

    @OneToMany(mappedBy = "query", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Alert> alerts = new ArrayList<>();

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Query() {
    }

    public Query(String name, String definitionJson, Frequency frequency, Project project, ProjectTable projectTable) {
        this.name = name;
        this.definitionJson = definitionJson;
        this.frequency = frequency;
        this.project = project;
        this.projectTable = projectTable;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefinitionJson() {
        return definitionJson;
    }

    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ProjectTable getProjectTable() {
        return projectTable;
    }

    public void setProjectTable(ProjectTable projectTable) {
        this.projectTable = projectTable;
    }

    public List<Alert> getAlerts() {
        return alerts;
    }

    public void setAlerts(List<Alert> alerts) {
        this.alerts = alerts;
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
        if (!(o instanceof Query)) return false;
        Query query = (Query) o;
        return Objects.equals(id, query.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Query{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", frequency=" + frequency +
                ", active=" + active +
                '}';
    }
}
