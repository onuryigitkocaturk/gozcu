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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
            throw new DuplicateGroupException("Grup zaten mevcut: " + request.getName());
        }

        Group group = new Group(request.getName(), request.getDescription());
        return groupRepository.save(group);
    }

    @Override
    @Transactional
    public void deleteGroup(UUID id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new GroupNotFoundException("Grup bulunamadı: " + id));

        // owning side User; grubu direkt silmeden önce üyelerin
        // koleksiyonundan çıkarmazsak user_group'ta FK ihlali olur.
        for (User user : new HashSet<>(group.getUsers())) {
            user.getGroups().remove(group);
            userRepository.save(user);
        }

        groupRepository.delete(group);
    }

    @Override
    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    @Override
    public void addUserToGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grup bulunamadı: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));

        user.getGroups().add(group);
        userRepository.save(user);
    }

    @Override
    public void removeUserFromGroup(UUID groupId, UUID userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grup bulunamadı: " + groupId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı: " + userId));

        user.getGroups().remove(group);
        userRepository.save(user);
    }

    @Override
    public List<User> getGroupUsers(UUID groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Grup bulunamadı: " + groupId));
        return new ArrayList<>(group.getUsers());
    }
}
