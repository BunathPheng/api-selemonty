package org.hrd.finalprojectmuseum.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private UUID notificationId;
    private UUID subscriptionId;
    private String subscriptionCode; // OneSignal Player ID
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
