package com.onuryigitkocaturk.query_monitor.service;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.model.Group;

public interface GroupService {

    Group createGroup(GroupRequest request);

    void deleteGroup(Long id);

    void addUserToGroup(Long groupId, Long userId);

    void removeUserFromGroup(Long groupId, Long userId);
}
