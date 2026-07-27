package com.onuryigitkocaturk.query_monitor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onuryigitkocaturk.query_monitor.connector.TableMetadataService;
import com.onuryigitkocaturk.query_monitor.dto.QueryRequest;
import com.onuryigitkocaturk.query_monitor.exception.InvalidQueryDefinitionException;
import com.onuryigitkocaturk.query_monitor.exception.ProjectNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.QueryNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.TableNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Project;
import com.onuryigitkocaturk.query_monitor.model.ProjectTable;
import com.onuryigitkocaturk.query_monitor.model.Query;
import com.onuryigitkocaturk.query_monitor.querybuilder.QueryDefinitionValidator;
import com.onuryigitkocaturk.query_monitor.repository.ProjectRepository;
import com.onuryigitkocaturk.query_monitor.repository.ProjectTableRepository;
import com.onuryigitkocaturk.query_monitor.repository.QueryRepository;
import com.onuryigitkocaturk.query_monitor.service.QueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QueryServiceImpl implements QueryService {

    private final QueryRepository queryRepository;
    private final ProjectRepository projectRepository;
    private final ProjectTableRepository projectTableRepository;
    private final TableMetadataService tableMetadataService;
    private final QueryDefinitionValidator queryDefinitionValidator;
    private final ObjectMapper objectMapper;

    public QueryServiceImpl(QueryRepository queryRepository,
                             ProjectRepository projectRepository,
                             ProjectTableRepository projectTableRepository,
                             TableMetadataService tableMetadataService,
                             QueryDefinitionValidator queryDefinitionValidator,
                             ObjectMapper objectMapper) {
        this.queryRepository = queryRepository;
        this.projectRepository = projectRepository;
        this.projectTableRepository = projectTableRepository;
        this.tableMetadataService = tableMetadataService;
        this.queryDefinitionValidator = queryDefinitionValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    public Query createQuery(Long projectId, Long projectTableId, QueryRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found: " + projectId));

        ProjectTable projectTable = getValidatedProjectTable(projectId, projectTableId);

        List<String> validColumns = tableMetadataService.listColumns(projectTable.getTableName());
        queryDefinitionValidator.validate(request.getDefinition(), validColumns);

        String definitionJson;
        try {
            definitionJson = objectMapper.writeValueAsString(request.getDefinition());
        } catch (JsonProcessingException e) {
            throw new InvalidQueryDefinitionException("Query definition could not be processed");
        }

        Query query = new Query(request.getName(), definitionJson, request.getFrequency(), project, projectTable);
        query.setActive(true);
        return queryRepository.save(query);
    }

    @Override
    public void deleteQuery(Long projectId, Long queryId) {
        Query query = queryRepository.findById(queryId)
                .orElseThrow(() -> new QueryNotFoundException("Query not found: " + queryId));

        if (!query.getProject().getId().equals(projectId)) {
            throw new QueryNotFoundException("Query not found: " + queryId);
        }

        queryRepository.delete(query);
    }

    @Override
    public List<Query> getQueriesForTable(Long projectId, Long projectTableId) {
        getValidatedProjectTable(projectId, projectTableId);
        return queryRepository.findByProjectTableId(projectTableId);
    }

    private ProjectTable getValidatedProjectTable(Long projectId, Long projectTableId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectNotFoundException("Project not found: " + projectId);
        }

        ProjectTable projectTable = projectTableRepository.findById(projectTableId)
                .orElseThrow(() -> new TableNotFoundException("Project table not found: " + projectTableId));

        if (!projectTable.getProject().getId().equals(projectId)) {
            throw new TableNotFoundException("This table does not belong to the given project");
        }

        return projectTable;
    }
}
