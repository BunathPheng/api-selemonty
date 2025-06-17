package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class Subscriptions {
    private UUID subscriptionId;
    private UUID userId;
    private String subscriptionCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AppUserRegister user;
    private List<NotificationMessage> notificationMessages;
}
