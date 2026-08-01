package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

// bir koşulun değeri iki farklı türde olabilir: sabit bir değer -> literal
// ya da her runda yeniden hesaplanan göreli değer -> relative
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LiteralValue.class, name = "LITERAL"),
        @JsonSubTypes.Type(value = RelativeDateValue.class, name = "RELATIVE_DATE")
})
public interface ConditionValue {
}
