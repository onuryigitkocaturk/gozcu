package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/connector")
@PreAuthorize("hasRole('ADMIN')")
public class ConnectorController {

    private final TableMetadataService tableMetadataService;

    public ConnectorController(TableMetadataService tableMetadataService) {
        this.tableMetadataService = tableMetadataService;
    }

    @GetMapping("/tables")
    public ResponseEntity<List<String>> listTables() {
        return ResponseEntity.ok(tableMetadataService.listTables());
    }

    @GetMapping("/tables/{tableName}/columns")
    public ResponseEntity<List<String>> listColumns(@PathVariable String tableName) {
        return ResponseEntity.ok(tableMetadataService.listColumns(tableName));
    }
}
