package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.dto.querydefinition.QueryNode;
import com.onuryigitkocaturk.query_monitor.enums.Frequency;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QueryRequest {

    @NotBlank
    private String name;

    @NotNull
    private Frequency frequency;

    @NotNull
    @Valid
    private QueryNode definition;

    public QueryRequest() {
    }

    public QueryRequest(String name, Frequency frequency, QueryNode definition) {
        this.name = name;
        this.frequency = frequency;
        this.definition = definition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public QueryNode getDefinition() {
        return definition;
    }

    public void setDefinition(QueryNode definition) {
        this.definition = definition;
    }
}
