package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.dto.GroupResponse;
import com.onuryigitkocaturk.query_monitor.dto.UserResponse;
import com.onuryigitkocaturk.query_monitor.mapper.GroupMapper;
import com.onuryigitkocaturk.query_monitor.mapper.UserMapper;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('ADMIN')")
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;
    private final UserMapper userMapper;

    public GroupController(GroupService groupService, GroupMapper groupMapper, UserMapper userMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
        this.userMapper = userMapper;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        Group group = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupMapper.toResponse(group));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getAllGroups() {
        List<GroupResponse> response = groupService.getAllGroups().stream()
                .map(groupMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable UUID id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable UUID groupId, @PathVariable UUID userId) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{groupId}/users")
    public ResponseEntity<List<UserResponse>> getGroupUsers(@PathVariable UUID groupId) {
        List<UserResponse> response = groupService.getGroupUsers(groupId).stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
