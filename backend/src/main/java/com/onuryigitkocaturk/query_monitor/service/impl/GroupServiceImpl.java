package com.onuryigitkocaturk.query_monitor.service.impl;

import com.onuryigitkocaturk.query_monitor.dto.GroupRequest;
import com.onuryigitkocaturk.query_monitor.exception.DuplicateGroupException;
import com.onuryigitkocaturk.query_monitor.exception.GroupNotFoundException;
import com.onuryigitkocaturk.query_monitor.exception.UserNotFoundException;
import com.onuryigitkocaturk.query_monitor.model.Group;
import com.onuryigitkocaturk.query_monitor.model.User;
import com.onuryigitkocaturk.query_monitor.repository.GroupRepository;
import com.onuryigitkocaturk.query_monitor.repository.UserRepository;
import com.onuryigitkocaturk.query_monitor.service.GroupService;
import org.springframework.stereotype.Service;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public GroupServiceImpl(GroupRepository groupRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Group createGroup(GroupRequest request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new DuplicateGroupException("Group already exists: " + request.getName());
        }

        Group group = new Group(request.getName(), request.getDescription());
        return groupRepository.save(group);
    }

    @Override
    public void deleteGroup(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new GroupNotFoundException("Group not found: " + id);
        }
        groupRepository.deleteById(id);
    }

    @Override
    public void addUserToGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.getGroups().add(group);
        userRepository.save(user);
    }

    @Override
    public void removeUserFromGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.getGroups().remove(group);
        userRepository.save(user);
    }
}
