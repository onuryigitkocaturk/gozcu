package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.model.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface QueryService {

    Query createQuery(UUID projectId, UUID projectTableId, QueryRequest request, UUID createdByUserId);

    Query updateQuery(UUID projectId, UUID queryId, QueryRequest request);

    void deleteQuery(UUID projectId, UUID queryId);

    List<Query> getQueriesForTable(UUID projectId, UUID projectTableId);

    List<Map<String, Object>> runQuery(UUID projectId, UUID queryId);

    long countQueryMatches(UUID projectId, UUID queryId);

    byte[] exportQueryResultAsExcel(UUID projectId, UUID queryId);

    byte[] exportQueryResultAsPdf(UUID projectId, UUID queryId);
}
