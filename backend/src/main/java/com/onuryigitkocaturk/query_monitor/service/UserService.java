package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.dto.UpdateUserRequest;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;

public interface UserService {

    User register(RegisterRequest request);

    User getByUsername(String username);

    User updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    List<User> getAllUsers();
}
