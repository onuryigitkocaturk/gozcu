package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;
import java.util.UUID;

public interface GroupService {

    Group createGroup(GroupRequest request);

    void deleteGroup(UUID id);

    List<Group> getAllGroups();

    void addUserToGroup(UUID groupId, UUID userId);

    void removeUserFromGroup(UUID groupId, UUID userId);

    List<User> getGroupUsers(UUID groupId);
}
