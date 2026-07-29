package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.dto.QueryResponse;
import com.onuryigitkocaturk.query_monitor.mapper.QueryMapper;
import com.onuryigitkocaturk.query_monitor.model.Query;
import com.onuryigitkocaturk.query_monitor.security.UserDetailsImpl;
import com.onuryigitkocaturk.query_monitor.service.QueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects/{projectId}/tables/{tableId}/queries")
@PreAuthorize("hasRole('ADMIN')")
public class QueryController {

    private final QueryService queryService;
    private final QueryMapper queryMapper;

    public QueryController(QueryService queryService, QueryMapper queryMapper) {
        this.queryService = queryService;
        this.queryMapper = queryMapper;
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @PostMapping
    public ResponseEntity<QueryResponse> createQuery(@PathVariable UUID projectId,
                                                       @PathVariable UUID tableId,
                                                       @Valid @RequestBody QueryRequest request,
                                                       @AuthenticationPrincipal UserDetailsImpl principal) {
        Query query = queryService.createQuery(projectId, tableId, request, principal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(queryMapper.toResponse(query));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping
    public ResponseEntity<List<QueryResponse>> getQueries(@PathVariable UUID projectId,
                                                            @PathVariable UUID tableId) {
        List<Query> queries = queryService.getQueriesForTable(projectId, tableId);
        List<QueryResponse> response = queries.stream()
                .map(queryMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{queryId}")
    public ResponseEntity<Void> deleteQuery(@PathVariable UUID projectId,
                                              @PathVariable UUID tableId,
                                              @PathVariable UUID queryId) {
        queryService.deleteQuery(projectId, queryId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{queryId}/run")
    public ResponseEntity<List<Map<String, Object>>> runQuery(@PathVariable UUID projectId,
                                                                @PathVariable UUID tableId,
                                                                @PathVariable UUID queryId) {
        return ResponseEntity.ok(queryService.runQuery(projectId, queryId));
    }

    @PreAuthorize("hasRole('ADMIN') or @userRepository.existsByIdAndProjects_Id(principal.id, #projectId)")
    @GetMapping("/{queryId}/count")
    public ResponseEntity<Map<String, Long>> countQueryMatches(@PathVariable UUID projectId,
                                                                  @PathVariable UUID tableId,
                                                                  @PathVariable UUID queryId) {
        return ResponseEntity.ok(Map.of("count", queryService.countQueryMatches(projectId, queryId)));
    }
}
