package org.hrd.finalprojectmuseum.service;

import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface OneSignalService {

    // Subscription management
    Subscriptions subscribeUser(UUID userId, String oneSignalPlayerId);
    void unsubscribeUser(UUID userId, String oneSignalPlayerId);
    void unsubscribeUserFromAllDevices(UUID userId);
    List<Subscriptions> getUserSubscriptions(UUID userId);
    List<Subscriptions> getUserSubscriptionsWithDetails(UUID userId);
    Subscriptions getSubscriptionByPlayerId(String playerId);
    void updateSubscription(Subscriptions subscription);
    boolean canUserReceiveNotifications(UUID userId);
    List<String> getUserSubscriptionCodes(UUID userId);

    // Notification sending
    Mono<String> sendToUser(UUID userId, String title, String message);
    Mono<String> sendToUsers(List<UUID> userIds, String title, String message);
    Mono<String> sendToAllUsers(String title, String message);
    Mono<String> sendToVerifiedUsers(String title, String message);
    Mono<String> sendWithCustomData(UUID userId, String title, String message,
                                    Map<String, Object> customData, String url);

    // Notification history management
    List<NotificationMessage> getUserNotifications(UUID userId);
    List<NotificationMessage> getUserNotificationsWithDetails(UUID userId);
    List<NotificationMessage> getUserUnreadNotifications(UUID userId);
    NotificationMessage getNotificationById(UUID notificationId);
    void markNotificationAsRead(UUID notificationId);
    void markNotificationAsUnread(UUID notificationId);
    void markAllUserNotificationsAsRead(UUID userId);
    void deleteNotification(UUID notificationId);

    // Statistics
    int getTotalSubscriberCount();
    int getUserUnreadCount(UUID userId);
}