package com.onuryigitkocaturk.query_monitor.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.querybuilder.QuerySqlBuilder;
import com.onuryigitkocaturk.query_monitor.querybuilder.SqlFragment;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// JSON'daki koşulu SQL'e çevirip izlenen tabloda çalıştırıyor: executeQuery satırları,
// countMatches kaç satır eşleştiğini döndürüyor.
@Service
public class QueryExecutionService {

    private final JdbcTemplate monitoredJdbcTemplate;
    private final QuerySqlBuilder sqlBuilder;
    private final ObjectMapper objectMapper;

    public QueryExecutionService(@Qualifier("monitoredJdbcTemplate") JdbcTemplate monitoredJdbcTemplate,
                                  QuerySqlBuilder sqlBuilder,
                                  ObjectMapper objectMapper) {
        this.monitoredJdbcTemplate = monitoredJdbcTemplate;
        this.sqlBuilder = sqlBuilder;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> executeQuery(String tableName, String definitionJson) {
        SqlFragment fragment = buildFragment(definitionJson);
        String sql = "SELECT * FROM " + tableName + " WHERE " + fragment.sql();
        return monitoredJdbcTemplate.queryForList(sql, fragment.parameters().toArray());
    }

    public long countMatches(String tableName, String definitionJson) {
        SqlFragment fragment = buildFragment(definitionJson);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + fragment.sql();
        Long count = monitoredJdbcTemplate.queryForObject(sql, Long.class, fragment.parameters().toArray());
        return count != null ? count : 0L;
    }

    private SqlFragment buildFragment(String definitionJson) {
        try {
            QueryNode root = objectMapper.readValue(definitionJson, QueryNode.class);
            return sqlBuilder.build(root);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Stored query definition is corrupted", e);
        }
    }
}
