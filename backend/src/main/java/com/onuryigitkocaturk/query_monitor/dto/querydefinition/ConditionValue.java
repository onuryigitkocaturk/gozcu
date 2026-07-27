package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Bir kosulun degeri iki farkli "tur"de (kind) olabilir: sabit bir deger
 * (LiteralValue) ya da her calistirmada yeniden hesaplanan goreli bir
 * tarih (RelativeDateValue, orn. "bugunden 30 gun sonra").
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = LiteralValue.class, name = "LITERAL"),
        @JsonSubTypes.Type(value = RelativeDateValue.class, name = "RELATIVE_DATE")
})
public interface ConditionValue {
}
