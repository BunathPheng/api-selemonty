package org.hrd.finalprojectmuseum.service.impl;

import org.hrd.finalprojectmuseum.config.OneSignalConfig;
import org.hrd.finalprojectmuseum.model.dto.request.NotificationRequest;
import org.hrd.finalprojectmuseum.model.entity.AppUserRegister;
import org.hrd.finalprojectmuseum.model.entity.NotificationMessage;
import org.hrd.finalprojectmuseum.model.entity.Subscriptions;
import org.hrd.finalprojectmuseum.repository.AppUserRepository;
import org.hrd.finalprojectmuseum.repository.NotificationMessageRepository;
import org.hrd.finalprojectmuseum.repository.ProfileRepository;
import org.hrd.finalprojectmuseum.repository.SubscriptionRepository;
import org.hrd.finalprojectmuseum.service.OneSignalService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class OneSignalServiceImpl implements OneSignalService {

    private static final Logger logger = LoggerFactory.getLogger(OneSignalServiceImpl.class);
    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private OneSignalConfig oneSignalConfig;

    private final WebClient webClient;
    @Autowired
    private NotificationMessageRepository notificationMessageRepository;

    public OneSignalServiceImpl() {
        this.webClient = WebClient.builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Subscriptions subscribeUser(UUID userId, String oneSignalPlayerId) {
        AppUserRegister user = appUserRepository.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Check if subscription already exists
        Subscriptions existingSubscription = subscriptionRepository.findBySubscriptionCode(oneSignalPlayerId);
        if (existingSubscription != null) {
            return existingSubscription;
        }

        Subscriptions subscription = new Subscriptions();
        subscription.setSubscriptionCode(oneSignalPlayerId);
        subscription.setUserId(userId);
        subscriptionRepository.insert(subscription);
        return subscription;
    }

    /**
     * Send notification to all subscribed users
     */
    public Mono<String> sendToAllUsers(String title, String message) {
        List<String> allPlayerIds = subscriptionRepository.findAllSubscriptionCodes();

        if (allPlayerIds.isEmpty()) {
            return Mono.just("No subscribers found");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(allPlayerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, allPlayerIds));
    }

    public Mono<String> sendToUser(UUID userId, String title, String message) {
        List<String> playerIds = subscriptionRepository.findSubscriptionCodesByUserId(userId);

        if (playerIds.isEmpty()) {
            return Mono.just("User has no active subscriptions");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, playerIds));
    }

    /**
     * Send notification to multiple users
     */
    public Mono<String> sendToUsers(List<UUID> userIds, String title, String message) {
        List<String> playerIds = new ArrayList<>();

        for (UUID userId : userIds) {
            playerIds.addAll(subscriptionRepository.findSubscriptionCodesByUserId(userId));
        }

        if (playerIds.isEmpty()) {
            return Mono.just("No active subscriptions found for specified users");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, playerIds));
    }

    /**
     * Send notification with custom data
     */
    public Mono<String> sendWithCustomData(UUID userId, String title, String message,
                                           Map<String, Object> customData, String url) {
        List<String> playerIds = subscriptionRepository.findSubscriptionCodesByUserId(userId);

        if (playerIds.isEmpty()) {
            return Mono.just("User has no active subscriptions");
        }

        NotificationRequest request = new NotificationRequest();
        request.setAppId(oneSignalConfig.getAppId());
        request.setIncludePlayerIds(playerIds);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        request.setContents(contents);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        request.setHeadings(headings);

        if (customData != null) {
            request.setData(customData);
        }

        if (url != null) {
            request.setUrl(url);
        }

        return sendNotification(request)
                .doOnSuccess(response -> saveNotificationToDatabase(title, message, playerIds));
    }

    /**
     * Send notification to verified users only
     */
    public Mono<String> sendToVerifiedUsers(String title, String message) {
        List<AppUserRegister> verifiedUsers = appUserRepository.findByIsVerified(true);
        List<UUID> userIds = verifiedUsers.stream()
                .map(AppUserRegister::getUserId)
                .toList();

        return sendToUsers(userIds, title, message);
    }

    /**
     * Core method to send notification via OneSignal API
     */
    private Mono<String> sendNotification(NotificationRequest request) {
        return webClient.post()
                .uri(oneSignalConfig.getApiUrl())
                .header("Authorization", "Basic " + oneSignalConfig.getRestApiKey())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> logger.info("Notification sent successfully: {}", response))
                .doOnError(error -> logger.error("Failed to send notification: {}", error.getMessage()));
    }

    /**
     * Save notification to database for tracking
     */
    private void saveNotificationToDatabase(String title, String message, List<String> playerIds) {
        for (String playerId : playerIds) {
            Subscriptions subscription = subscriptionRepository.findBySubscriptionCode(playerId);
            if (subscription != null) {
                NotificationMessage notificationMessage = NotificationMessage.builder()
                        .subscriptionId(subscription.getSubscriptionId())
                        .title(title)
                        .message(message)
                        .build();
                notificationMessageRepository.insert(notificationMessage);
            }
        }
    }

    /**
     * Get user's notification history
     */
    public List<NotificationMessage> getUserNotifications(UUID userId) {
        return notificationMessageRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get user's notification history with full details
     */
    public List<NotificationMessage> getUserNotificationsWithDetails(UUID userId) {
        return notificationMessageRepository.findByUserIdWithDetails(userId);
    }

    /**
     * Get unread notifications for user
     */
    public List<NotificationMessage> getUserUnreadNotifications(UUID userId) {
        return notificationMessageRepository.findUnreadByUserId(userId);
    }

    /**
     * Mark notification as read
     */
    public void markNotificationAsRead(UUID notificationId) {
        notificationMessageRepository.updateReadStatus(notificationId, true);
    }

    /**
     * Mark notification as unread
     */
    public void markNotificationAsUnread(UUID notificationId) {
        notificationMessageRepository.updateReadStatus(notificationId, false);
    }

    /**
     * Mark all user notifications as read
     */
    public void markAllUserNotificationsAsRead(UUID userId) {
        notificationMessageRepository.markAllAsReadByUserId(userId);
    }

    /**
     * Get notification by ID
     */
    public NotificationMessage getNotificationById(UUID notificationId) {
        return notificationMessageRepository.findById(notificationId);
    }

    /**
     * Delete notification
     */
    public void deleteNotification(UUID notificationId) {
        notificationMessageRepository.deleteById(notificationId);
    }

    /**
     * Unsubscribe user
     */
    public void unsubscribeUser(UUID userId, String oneSignalPlayerId) {
        subscriptionRepository.deleteByUserIdAndSubscriptionCode(userId, oneSignalPlayerId);
    }

    /**
     * Unsubscribe user from all devices
     */
    public void unsubscribeUserFromAllDevices(UUID userId) {
        List<Subscriptions> subscriptions = subscriptionRepository.findByUserId(userId);
        for (Subscriptions subscription : subscriptions) {
            subscriptionRepository.deleteById(subscription.getSubscriptionId());
        }
    }

    /**
     * Get user's active subscriptions
     */
    public List<Subscriptions> getUserSubscriptions(UUID userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    /**
     * Get user's subscriptions with user details
     */
    public List<Subscriptions> getUserSubscriptionsWithDetails(UUID userId) {
        return subscriptionRepository.findByUserIdWithUser(userId);
    }

    /**
     * Get subscription by player ID
     */
    public Subscriptions getSubscriptionByPlayerId(String playerId) {
        return subscriptionRepository.findBySubscriptionCode(playerId);
    }

    /**
     * Update subscription
     */
    public void updateSubscription(Subscriptions subscription) {
        subscription.setUpdatedAt(LocalDateTime.now());
        subscriptionRepository.update(subscription);
    }

    /**
     * Get total count of subscribers
     */
    public int getTotalSubscriberCount() {
        return subscriptionRepository.findAllSubscriptionCodes().size();
    }

    /**
     * Get unread notification count for user
     */
    public int getUserUnreadCount(UUID userId) {
        return getUserUnreadNotifications(userId).size();
    }
}