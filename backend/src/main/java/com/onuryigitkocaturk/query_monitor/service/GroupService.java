package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.model.User;

import java.util.List;

public interface GroupService {

    Group createGroup(GroupRequest request);

    void deleteGroup(Long id);

    List<Group> getAllGroups();

    void addUserToGroup(Long groupId, Long userId);

    void removeUserFromGroup(Long groupId, Long userId);

    List<User> getGroupUsers(Long groupId);
}
