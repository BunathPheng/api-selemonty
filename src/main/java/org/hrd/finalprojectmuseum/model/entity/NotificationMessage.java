package org.hrd.finalprojectmuseum.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMessage {
    private String notificationMessageId;
    private String subscriptionId;
    private String userId;
    private String title;
    private String message;
    private String notificationType; // info, warning, success, error
    private String priority; // low, normal, high
    private Boolean isRead;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private String onesignalId;
    private LocalDateTime createdAt;
}
