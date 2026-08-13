package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.ChangeRoleRequest;
import com.onuryigitkocaturk.query_monitor.dto.MyAccountResponse;
import com.onuryigitkocaturk.query_monitor.dto.UpdateUserRequest;
import com.onuryigitkocaturk.query_monitor.dto.UserResponse;
import com.onuryigitkocaturk.query_monitor.mapper.UserMapper;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.security.UserDetailsImpl;
import com.onuryigitkocaturk.query_monitor.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class
UserManagementController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserManagementController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserDetailsImpl principal) {
        User user = userService.getByUsername(principal.getUsername());
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @GetMapping("/me/account")
    public ResponseEntity<MyAccountResponse> getMyAccountOverview(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(userService.getMyAccountOverview(principal.getId()));
    }

    @DeleteMapping("/me/devices/{deviceId}")
    public ResponseEntity<Void> removeTrustedDevice(@PathVariable UUID deviceId,
                                                      @AuthenticationPrincipal UserDetailsImpl principal) {
        userService.removeTrustedDevice(principal.getId(), deviceId);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> responses = userService.getAllUsers().stream()
                .map(userMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        User user = userService.updateUser(id, request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/role")
    public ResponseEntity<UserResponse> changeRole(@PathVariable UUID id, @Valid @RequestBody ChangeRoleRequest request) {
        User user = userService.changeRole(id, request);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }
}
