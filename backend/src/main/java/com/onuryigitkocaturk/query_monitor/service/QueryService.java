package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.model.Query;

import java.util.List;

public interface QueryService {

    Query createQuery(Long projectId, Long projectTableId, QueryRequest request);

    void deleteQuery(Long projectId, Long queryId);

    List<Query> getQueriesForTable(Long projectId, Long projectTableId);
}
