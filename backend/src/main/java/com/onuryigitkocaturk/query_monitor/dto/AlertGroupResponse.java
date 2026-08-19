package com.onuryigitkocaturk.query_monitor.dto;

import java.util.UUID;

// AlertResponse icindeki grup listesinde kullanilan hafif ozet - GroupResponse'un
// tamamina (description, createdAt) gerek yok, sadece id+name yeterli.
public class AlertGroupResponse {

    private UUID id;
    private String name;

    public AlertGroupResponse() {
    }

    public AlertGroupResponse(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
