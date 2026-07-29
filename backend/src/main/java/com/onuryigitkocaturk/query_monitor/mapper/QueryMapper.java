package com.onuryigitkocaturk.query_monitor.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.dto.QueryResponse;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.model.Query;
import org.springframework.stereotype.Component;

@Component
public class QueryMapper {

    private final ObjectMapper objectMapper;

    public QueryMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public QueryResponse toResponse(Query query) {
        QueryNode definition;
        try {
            definition = objectMapper.readValue(query.getDefinitionJson(), QueryNode.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored query definition is corrupted for query id " + query.getId(), e);
        }

        return new QueryResponse(
                query.getId(),
                query.getName(),
                query.getFrequency(),
                query.isActive(),
                query.getProject().getId(),
                query.getProjectTable().getId(),
                query.getProjectTable().getTableName(),
                definition,
                query.getCreatedAt(),
                query.getCreatedBy() != null ? query.getCreatedBy().getUsername() : null
        );
    }
}
