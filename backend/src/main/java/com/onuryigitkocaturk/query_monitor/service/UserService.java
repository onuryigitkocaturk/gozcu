package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.LoginRequest;
import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.model.User;

public interface UserService {

    User register(RegisterRequest request);

    User login(LoginRequest request);
}
