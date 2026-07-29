package com.onuryigitkocaturk.query_monitor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * DIKKAT: model.Query (entity) ile Spring Data'nin @Query (JPQL) anotasyonu
 * ayni basit isme sahip oldugu icin, ikisini ayni anda import edemiyoruz.
 * Bu yuzden entity'yi asagida her yerde tam paket yoluyla
 * (com.onuryigitkocaturk.query_monitor.model.Query) kullanıyoruz.
 */
public interface QueryRepository
        extends JpaRepository<com.onuryigitkocaturk.query_monitor.model.Query, UUID> {

    List<com.onuryigitkocaturk.query_monitor.model.Query> findByProjectId(UUID projectId);

    List<com.onuryigitkocaturk.query_monitor.model.Query> findByActiveTrue();

    @Query("SELECT q FROM Query q JOIN FETCH q.project JOIN FETCH q.projectTable " +
            "LEFT JOIN FETCH q.createdBy WHERE q.projectTable.id = :projectTableId")
    List<com.onuryigitkocaturk.query_monitor.model.Query> findByProjectTableId(
            @Param("projectTableId") UUID projectTableId);
}
