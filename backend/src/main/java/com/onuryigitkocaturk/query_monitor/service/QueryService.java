package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.model.Query;

import java.util.List;
import java.util.Map;

public interface QueryService {

    Query createQuery(Long projectId, Long projectTableId, QueryRequest request, Long createdByUserId);

    void deleteQuery(Long projectId, Long queryId);

    List<Query> getQueriesForTable(Long projectId, Long projectTableId);

    List<Map<String, Object>> runQuery(Long projectId, Long queryId);

    long countQueryMatches(Long projectId, Long queryId);
}
