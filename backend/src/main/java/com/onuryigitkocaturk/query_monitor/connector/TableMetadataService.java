package com.onuryigitkocaturk.query_monitor.connector;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableMetadataService {

    private final JdbcTemplate monitoredJdbcTemplate;

    public TableMetadataService(@Qualifier("monitoredJdbcTemplate") JdbcTemplate monitoredJdbcTemplate) {
        this.monitoredJdbcTemplate = monitoredJdbcTemplate;
    }

    public List<String> listTables() {
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = 'public' ORDER BY table_name";
        return monitoredJdbcTemplate.queryForList(sql, String.class);
    }

    public List<String> listColumns(String tableName) {
        String sql = "SELECT column_name FROM information_schema.columns " +
                "WHERE table_schema = 'public' AND table_name = ? ORDER BY ordinal_position";
        return monitoredJdbcTemplate.queryForList(sql, String.class, tableName);
    }
}
