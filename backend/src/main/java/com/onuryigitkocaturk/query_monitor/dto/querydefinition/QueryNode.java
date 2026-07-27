package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Surukle-birak query builder'in urettigi agac yapisinin kok arayuzu.
 * Jackson, JSON'daki "type" alanina bakarak GroupNode/ConditionNode'dan
 * hangisini olusturacagina karar verir (polimorfik deserializasyon).
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = GroupNode.class, name = "GROUP"),
        @JsonSubTypes.Type(value = ConditionNode.class, name = "CONDITION")
})
public interface QueryNode {
}
