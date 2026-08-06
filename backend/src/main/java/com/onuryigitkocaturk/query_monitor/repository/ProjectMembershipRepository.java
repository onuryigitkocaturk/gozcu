package com.onuryigitkocaturk.query_monitor.repository;

import com.onuryigitkocaturk.query_monitor.enums.ProjectRole;
import com.onuryigitkocaturk.query_monitor.model.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, UUID> {

    @Query("SELECT pm FROM ProjectMembership pm JOIN FETCH pm.user WHERE pm.project.id = :projectId")
    List<ProjectMembership> findByProjectId(@Param("projectId") UUID projectId);

    @Query("SELECT pm FROM ProjectMembership pm JOIN FETCH pm.project WHERE pm.user.id = :userId")
    List<ProjectMembership> findByUserId(@Param("userId") UUID userId);

    Optional<ProjectMembership> findByProjectIdAndUserId(UUID projectId, UUID userId);

    boolean existsByProjectIdAndUserIdAndRoleIn(UUID projectId, UUID userId, Collection<ProjectRole> roles);

    void deleteByProjectIdAndUserId(UUID projectId, UUID userId);

    void deleteByUserId(UUID userId);
}
