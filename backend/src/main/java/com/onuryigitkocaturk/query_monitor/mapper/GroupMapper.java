package com.onuryigitkocaturk.query_monitor.mapper;

import com.onuryigitkocaturk.query_monitor.dto.GroupResponse;
import com.onuryigitkocaturk.query_monitor.model.Group;
import org.springframework.stereotype.Component;

@Component
public class GroupMapper {

    public GroupResponse toResponse(Group group) {
        return new GroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getCreatedAt()
        );
    }
}
