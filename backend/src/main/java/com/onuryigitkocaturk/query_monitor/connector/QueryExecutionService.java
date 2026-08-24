package com.onuryigitkocaturk.query_monitor.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.querybuilder.QuerySqlBuilder;
import com.onuryigitkocaturk.query_monitor.querybuilder.SqlFragment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

// JSON'daki koşulu SQL'e çevirip izlenen tabloda çalıştırıyor: executeQuery satırları,
// countMatches kaç satır eşleştiğini döndürüyor.
@Service
public class QueryExecutionService {

    private final JdbcTemplateFactory jdbcTemplateFactory; // ConnectionDetails alıp, runtime'da çalışan bir JdbcTemplate
                                                           // üretmek.
    private final QuerySqlBuilder sqlBuilder;
    private final ObjectMapper objectMapper;

    public QueryExecutionService(JdbcTemplateFactory jdbcTemplateFactory,
                                  QuerySqlBuilder sqlBuilder,
                                  ObjectMapper objectMapper) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
        this.sqlBuilder = sqlBuilder;
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> executeQuery(ConnectionDetails connection, String tableName, String definitionJson) {
        SqlFragment fragment = buildFragment(definitionJson);
        String sql = "SELECT * FROM " + tableName + " WHERE " + fragment.sql();

        // Burada sql (içinde ?'ler olan string) ve fragment.parameters().toArray() (gerçek değerler dizisi) birlikte
        // JdbcTemplate'e veriliyor. JdbcTemplate, arka planda bir PreparedStatement oluşturup, parameters dizisindeki
        // her değeri sırasıyla (1. ?'ye 1. eleman, 2. ?'ye 2. eleman...) setObject() ile bağlıyor — bu, kod
        // tabanında görünmeyen, Spring'in JdbcTemplate'i ile JDBC driver'ının kendi içinde yaptığı iş.
        return jdbcTemplateFactory.create(connection).queryForList(sql, fragment.parameters().toArray());
    }

    public long countMatches(ConnectionDetails connection, String tableName, String definitionJson) {
        SqlFragment fragment = buildFragment(definitionJson);
        String sql = "SELECT COUNT(*) FROM " + tableName + " WHERE " + fragment.sql();
        Long count = jdbcTemplateFactory.create(connection)
                .queryForObject(sql, Long.class, fragment.parameters().toArray());
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
