package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// sürükle bırak query builder'ın ürettiği ağaç yapısının arayüzü.
// kendi başına alan/metot içermiyor, sadece "GroupNode" veya "ConditionNode" durumunu belirtiyor.
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GroupNode.class, name = "GROUP"),
        @JsonSubTypes.Type(value = ConditionNode.class, name = "CONDITION")
})
public interface QueryNode {
}
