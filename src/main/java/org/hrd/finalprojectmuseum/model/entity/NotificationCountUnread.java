package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class NotificationCountUnread {
    private Integer unreadCount;
    private LocalDateTime liveTime;
}
