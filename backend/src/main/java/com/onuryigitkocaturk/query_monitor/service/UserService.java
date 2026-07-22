package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.model.User;

public interface UserService {

    User register(RegisterRequest request);

    User getByUsername(String username);
}
