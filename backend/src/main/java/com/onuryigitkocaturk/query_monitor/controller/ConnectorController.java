package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.connector.ConnectionDetails;
import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.ConnectionTestRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// bir proje oluşturulurken girilen host/port/db bilgisiyle bağlantı testi yapar.
// proje oluştuktan sonra tablo keşfi ProjectController.discoverTables üzerinden yapılır.
@RestController // -> @ResponseBody + @Controller
@RequestMapping("/api/connector")
@PreAuthorize("hasRole('ADMIN')")
public class ConnectorController {

    private final TableMetadataService tableMetadataService;

    public ConnectorController(TableMetadataService tableMetadataService) {
        this.tableMetadataService = tableMetadataService;
    }

    @PostMapping("/test-connection")
    public ResponseEntity<List<String>> testConnection(@Valid @RequestBody ConnectionTestRequest request) {
        ConnectionDetails connection = new ConnectionDetails(
                request.getDbType(), request.getDbHost(), request.getDbPort(), request.getDbName(),
                request.getDbUsername(), request.getDbPassword());
        return ResponseEntity.ok(tableMetadataService.listTables(connection));
    }
}
