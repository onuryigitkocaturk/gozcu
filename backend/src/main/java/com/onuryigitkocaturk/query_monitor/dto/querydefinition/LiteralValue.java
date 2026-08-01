package com.onuryigitkocaturk.query_monitor.dto.querydefinition;

import jakarta.validation.constraints.NotNull;

// sabit bir değeri sarmalayan en basit sınıf
// value bilinçli olarak object, kolon tipi derleme zamanında bilinmiyor.
// jackson JSON'da "kind" : "LITERAL"  gördüğü anda bu sınıfı üretiyor.
public class LiteralValue implements ConditionValue {

    @NotNull
    private Object value;

    public LiteralValue() {
    }

    public LiteralValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
