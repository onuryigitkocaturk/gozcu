package com.onuryigitkocaturk.query_monitor.dto;

import com.onuryigitkocaturk.query_monitor.enums.Role;
import jakarta.validation.constraints.NotNull;

public class ChangeRoleRequest {

    @NotNull
    private Role role;

    public ChangeRoleRequest() {
    }

    public ChangeRoleRequest(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
