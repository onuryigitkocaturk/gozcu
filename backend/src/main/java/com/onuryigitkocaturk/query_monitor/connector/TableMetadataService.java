package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

// information_schema üzerinden tablo/kolon listeler ve bir tablonun tüm verisini döndürür;
// tableName whitelist ile önceden doğrulanmazsa SQL injection riski taşır.
@Service
public class TableMetadataService {

    private final JdbcTemplateFactory jdbcTemplateFactory;

    public TableMetadataService(JdbcTemplateFactory jdbcTemplateFactory) {
        this.jdbcTemplateFactory = jdbcTemplateFactory;
    }

    public List<String> listTables(ConnectionDetails connection) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.create(connection);
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = 'public' ORDER BY table_name";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> listColumns(ConnectionDetails connection, String tableName) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.create(connection);
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = ? ORDER BY ordinal_position";
        return jdbcTemplate.queryForList(sql, String.class, tableName);
    }

    /**
     * tableName burada bir SQL parametresi degil, bir identifier oldugu icin
     * '?' ile baglanamaz. Bu yuzden cagiran taraf, tableName'in gercekten
     * whitelist'te (ProjectTable) oldugunu bu metod cagrilmadan ONCE dogrulamak
     * zorundadir - aksi halde SQL injection riski olusur.
     */
    public List<Map<String, Object>> getTableData(ConnectionDetails connection, String tableName) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.create(connection);
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(sql);
    }
}
