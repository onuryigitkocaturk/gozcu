package com.onuryigitkocaturk.query_monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

// model.query (entity) ile Spring Data'nın @Query (JPQL) anotasyonu aynı basit
// isme sahip olduğundan aynı anda import edilemezler. Bu yüzden entity'yi
// her yerde tam paket yoluyla kullanıyoruz.
public interface QueryRepository extends JpaRepository<com.onuryigitkocaturk.query_monitor.model.Query, UUID> {

    List<com.onuryigitkocaturk.query_monitor.model.Query> findByProjectId(UUID projectId);

    List<com.onuryigitkocaturk.query_monitor.model.Query> findByActiveTrue();

    long countByProjectId(UUID projectId);

    long countByProjectIdAndActiveTrue(UUID projectId);

    long countByCreatedById(UUID userId);

    @Query("SELECT q FROM Query q JOIN FETCH q.project JOIN FETCH q.projectTable " +
            "LEFT JOIN FETCH q.createdBy WHERE q.projectTable.id = :projectTableId")
    List<com.onuryigitkocaturk.query_monitor.model.Query> findByProjectTableId(
            @Param("projectTableId") UUID projectTableId);

    // kullanıcı silinirken oluşturduğu query'lerin created_by_user_id'si nulla çekmek için
    @Modifying
    @Query("UPDATE Query q SET q.createdBy = null WHERE q.createdBy.id = :userId")
    void nullifyCreatedBy(@Param("userId") UUID userId);
}
