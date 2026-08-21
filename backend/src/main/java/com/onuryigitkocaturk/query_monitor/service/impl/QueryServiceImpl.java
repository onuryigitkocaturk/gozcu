package com.onuryigitkocaturk.query_monitor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionCredentialEncryptor;
import com.onuryigitkocaturk.query_monitor.connector.ConnectionDetails;
import com.onuryigitkocaturk.query_monitor.connector.QueryExecutionService;
import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.exception.InvalidQueryDefinitionException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.QueryNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.TableNotFoundException;
import com.onuryigitkocaturk.query_monitor.export.ExcelExportService;
import com.onuryigitkocaturk.query_monitor.export.PdfExportService;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.Query;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.querybuilder.QueryDefinitionValidator;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectTableRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.QueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class QueryServiceImpl implements QueryService {

    private final QueryRepository queryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTableRepository projectTableRepository;
    private final UserRepository userRepository;
    private final TableMetadataService tableMetadataService;
    private final QueryDefinitionValidator queryDefinitionValidator;
    private final QueryExecutionService queryExecutionService;
    private final ObjectMapper objectMapper;
    private final ConnectionCredentialEncryptor connectionCredentialEncryptor;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    public QueryServiceImpl(QueryRepository queryRepository,
                             ProjectRepository projectRepository,
                             ProjectTableRepository projectTableRepository,
                             UserRepository userRepository,
                             TableMetadataService tableMetadataService,
                             QueryDefinitionValidator queryDefinitionValidator,
                             QueryExecutionService queryExecutionService,
                             ObjectMapper objectMapper,
                             ConnectionCredentialEncryptor connectionCredentialEncryptor,
                            ExcelExportService excelExportService,
                            PdfExportService pdfExportService) {
        this.queryRepository = queryRepository;
        this.projectRepository = projectRepository;
        this.projectTableRepository = projectTableRepository;
        this.userRepository = userRepository;
        this.tableMetadataService = tableMetadataService;
        this.queryDefinitionValidator = queryDefinitionValidator;
        this.queryExecutionService = queryExecutionService;
        this.objectMapper = objectMapper;
        this.connectionCredentialEncryptor = connectionCredentialEncryptor;
        this.excelExportService = excelExportService;
        this.pdfExportService = pdfExportService;
    }

    @Override
    public Query createQuery(UUID projectId, UUID projectTableId, QueryRequest request, UUID createdByUserId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Proje bulunamadı: " + projectId));

        ProjectTable projectTable = getValidatedProjectTable(projectId, projectTableId);

        List<String> validColumns = tableMetadataService.listColumns(
                toConnectionDetails(project), projectTable.getTableName());
        queryDefinitionValidator.validate(request.getDefinition(), validColumns);

        String definitionJson;
        try {
            definitionJson = objectMapper.writeValueAsString(request.getDefinition());
        } catch (JsonProcessingException e) {
            throw new InvalidQueryDefinitionException("Sorgu tanımı işlenemedi");
        }

        Query query = new Query(request.getName(), definitionJson, request.getFrequency(), project, projectTable);
        query.setActive(true);
        userRepository.findById(createdByUserId).ifPresent(query::setCreatedBy);
        return queryRepository.save(query);
    }

    @Override
    public Query updateQuery(UUID projectId, UUID queryId, QueryRequest request) {
        Query query = getValidatedQuery(projectId, queryId);

        List<String> validColumns = tableMetadataService.listColumns(
                toConnectionDetails(query.getProject()), query.getProjectTable().getTableName());
        queryDefinitionValidator.validate(request.getDefinition(), validColumns);

        String definitionJson;
        try {
            definitionJson = objectMapper.writeValueAsString(request.getDefinition());
        } catch (JsonProcessingException e) {
            throw new InvalidQueryDefinitionException("Sorgu tanımı işlenemedi");
        }

        query.setName(request.getName());
        query.setDefinitionJson(definitionJson);
        query.setFrequency(request.getFrequency());
        return queryRepository.save(query);
    }

    @Override
    public void deleteQuery(UUID projectId, UUID queryId) {
        queryRepository.delete(getValidatedQuery(projectId, queryId));
    }

    @Override
    public List<Query> getQueriesForTable(UUID projectId, UUID projectTableId) {
        getValidatedProjectTable(projectId, projectTableId);
        return queryRepository.findByProjectTableId(projectTableId);
    }

    @Override
    public List<Map<String, Object>> runQuery(UUID projectId, UUID queryId) {
        Query query = getValidatedQuery(projectId, queryId);
        return queryExecutionService.executeQuery(
                toConnectionDetails(query.getProject()), query.getProjectTable().getTableName(), query.getDefinitionJson());
    }

    @Override
    public long countQueryMatches(UUID projectId, UUID queryId) {
        Query query = getValidatedQuery(projectId, queryId);
        return queryExecutionService.countMatches(
                toConnectionDetails(query.getProject()), query.getProjectTable().getTableName(), query.getDefinitionJson());
    }

    @Override
    public byte[] exportQueryResultAsExcel(UUID projectId, UUID queryId) {
        Query query = getValidatedQuery(projectId, queryId);
        List<Map<String, Object>> rows = queryExecutionService.executeQuery(toConnectionDetails(query.getProject()), query.getProjectTable().getTableName(), query.getDefinitionJson());
        return excelExportService.toExcel(query.getName(), rows);
    }

    @Override
    public byte[] exportQueryResultAsPdf(UUID projectId, UUID queryId) {
        Query query = getValidatedQuery(projectId, queryId);
        List<Map<String, Object>> rows = queryExecutionService.executeQuery(toConnectionDetails(query.getProject()), query.getProjectTable().getTableName(), query.getDefinitionJson());
        return pdfExportService.toPdf(query.getName(), rows);
    }


    private ConnectionDetails toConnectionDetails(Project project) {
        return new ConnectionDetails(
                project.getDbType(),
                project.getDbHost(),
                project.getDbPort(),
                project.getDbName(),
                project.getDbUsername(),
                connectionCredentialEncryptor.decrypt(project.getDbPasswordEncrypted()));
    }

    private Query getValidatedQuery(UUID projectId, UUID queryId) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new QueryNotFoundException("Sorgu bulunamadı: " + queryId));

        if (!query.getProject().getId().equals(projectId)) {
            throw new QueryNotFoundException("Sorgu bulunamadı: " + queryId);
        }

        return query;
    }

    private ProjectTable getValidatedProjectTable(UUID projectId, UUID projectTableId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Proje bulunamadı: " + projectId);
        }

        ProjectTable projectTable = projectTableRepository.findById(projectTableId)
                .orElseThrow(() -> new TableNotFoundException("Proje tablosu bulunamadı: " + projectTableId));

        if (!projectTable.getProject().getId().equals(projectId)) {
            throw new TableNotFoundException("Bu tablo verilen projeye ait değil");
        }

        return projectTable;
    }
}
