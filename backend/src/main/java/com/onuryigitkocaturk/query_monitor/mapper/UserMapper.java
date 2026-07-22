package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.UserResponse;
import com.onuryigitkocaturk.query_monitor.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
