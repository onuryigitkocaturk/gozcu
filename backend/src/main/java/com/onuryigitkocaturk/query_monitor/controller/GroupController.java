package com.onuryigitkocaturk.query_monitor.controller;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.dto.GroupResponse;
import com.onuryigitkocaturk.query_monitor.mapper.GroupMapper;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.service.GroupService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/groups")
@PreAuthorize("hasRole('ADMIN')")
public class GroupController {

    private final GroupService groupService;
    private final GroupMapper groupMapper;

    public GroupController(GroupService groupService, GroupMapper groupMapper) {
        this.groupService = groupService;
        this.groupMapper = groupMapper;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(@Valid @RequestBody GroupRequest request) {
        Group group = groupService.createGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(groupMapper.toResponse(group));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        groupService.deleteGroup(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> addUserToGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.addUserToGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{groupId}/users/{userId}")
    public ResponseEntity<Void> removeUserFromGroup(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeUserFromGroup(groupId, userId);
        return ResponseEntity.noContent().build();
    }
}
