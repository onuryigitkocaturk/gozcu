package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.ChangeRoleRequest;
import com.onuryigitkocaturk.query_monitor.dto.MyAccountResponse;
import com.onuryigitkocaturk.query_monitor.dto.RegisterRequest;
import com.onuryigitkocaturk.query_monitor.dto.UpdateUserRequest;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;
import java.util.UUID;

public interface UserService {

    User register(RegisterRequest request);

    User getByUsername(String username);

    User updateUser(UUID id, UpdateUserRequest request);

    void deleteUser(UUID id);

    List<User> getAllUsers();

    User changeRole(UUID id, ChangeRoleRequest request);

    MyAccountResponse getMyAccountOverview(UUID userId);

    void removeTrustedDevice(UUID userId, UUID deviceId);
}
