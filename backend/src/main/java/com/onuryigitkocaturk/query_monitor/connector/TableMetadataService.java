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
                "WHERE table_schema = ? ORDER BY table_name";
        return jdbcTemplate.queryForList(sql, String.class, connection.metadataSchema());
    }

    public List<String> listColumns(ConnectionDetails connection, String tableName) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.create(connection);
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_schema = ? AND table_name = ? ORDER BY ordinal_position";
        return jdbcTemplate.queryForList(sql, String.class, connection.metadataSchema(), tableName);
    }

   // Tablo adı bir değer değil, SQL'in yapısının kendisi olduğu için ? ile bağlanamaz — bu yüzden güvenlik,
   // ? yerine, tablo adının önceden onaylı bir whitelist'te (ProjectTable) olup olmadığının kontrol edilmesiyle sağlanıyor.
    public List<Map<String, Object>> getTableData(ConnectionDetails connection, String tableName) {
        JdbcTemplate jdbcTemplate = jdbcTemplateFactory.create(connection);
        String sql = "SELECT * FROM " + tableName;
        return jdbcTemplate.queryForList(sql);
    }
}
